"""Urban Pulse ML inference — forecasting + anomaly detection (M2).

All outputs are labeled PREDICTED / derived from SIMULATED history unless
observed data is provided. Never present predictions as measurements.
"""

from __future__ import annotations

from typing import Literal

import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from sklearn.ensemble import IsolationForest
from sklearn.linear_model import Ridge

app = FastAPI(
    title="Urban Pulse ML Inference",
    version="0.2.0",
    description="Baseline + Ridge forecast and IsolationForest anomalies for Harbor District demo.",
)


class SeriesPoint(BaseModel):
    t: int = Field(description="Unix epoch seconds")
    value: float


class ForecastRequest(BaseModel):
    segment_id: str
    history: list[SeriesPoint]
    horizon: int = Field(default=6, ge=1, le=48)
    model: Literal["naive_seasonal", "ridge"] = "ridge"


class ForecastPoint(BaseModel):
    t: int
    value: float
    source_kind: Literal["PREDICTED"] = "PREDICTED"
    model: str
    lower: float | None = None
    upper: float | None = None


class ForecastResponse(BaseModel):
    segment_id: str
    model: str
    baseline_mae: float | None
    model_mae: float | None
    notes: str
    predictions: list[ForecastPoint]


class AnomalyRequest(BaseModel):
    segment_id: str
    history: list[SeriesPoint]
    contamination: float = Field(default=0.08, ge=0.01, le=0.3)


class AnomalyPoint(BaseModel):
    t: int
    value: float
    score: float
    is_anomaly: bool
    source_kind: Literal["PREDICTED"] = "PREDICTED"


class AnomalyResponse(BaseModel):
    segment_id: str
    method: str
    anomaly_count: int
    notes: str
    points: list[AnomalyPoint]


def _values(history: list[SeriesPoint]) -> np.ndarray:
    return np.array([p.value for p in history], dtype=float)


def _naive_seasonal(values: np.ndarray, horizon: int, season: int = 6) -> np.ndarray:
    if len(values) == 0:
        return np.zeros(horizon)
    out = []
    for i in range(horizon):
        idx = len(values) - season + (i % season)
        if idx < 0:
            idx = len(values) - 1
        out.append(values[idx])
    return np.array(out)


def _ridge_forecast(values: np.ndarray, horizon: int, window: int = 6) -> tuple[np.ndarray, float | None, float | None]:
    if len(values) < window + 4:
        pred = _naive_seasonal(values, horizon)
        return pred, None, None

    X, y = [], []
    for i in range(window, len(values)):
        X.append(values[i - window : i])
        y.append(values[i])
    X_arr = np.asarray(X)
    y_arr = np.asarray(y)
    split = max(1, int(len(X_arr) * 0.8))
    X_train, X_val = X_arr[:split], X_arr[split:]
    y_train, y_val = y_arr[:split], y_arr[split:]

    model = Ridge(alpha=1.0)
    model.fit(X_train, y_train)

    baseline_mae = None
    model_mae = None
    if len(X_val):
        naive_val = np.array([X_val[i][-1] for i in range(len(X_val))])
        baseline_mae = float(np.mean(np.abs(naive_val - y_val)))
        model_mae = float(np.mean(np.abs(model.predict(X_val) - y_val)))

    hist = list(values)
    preds = []
    for _ in range(horizon):
        feat = np.array(hist[-window:]).reshape(1, -1)
        p = float(model.predict(feat)[0])
        preds.append(p)
        hist.append(p)
    return np.array(preds), baseline_mae, model_mae


@app.get("/health")
def health():
    return {"status": "ok", "service": "urban-pulse-ml", "milestone": "M2"}


@app.post("/v1/forecast", response_model=ForecastResponse)
def forecast(req: ForecastRequest):
    if len(req.history) < 3:
        raise HTTPException(400, "Need at least 3 history points")

    values = _values(req.history)
    last_t = req.history[-1].t
    step = 300
    if len(req.history) >= 2:
        step = max(60, req.history[-1].t - req.history[-2].t)

    if req.model == "naive_seasonal":
        preds = _naive_seasonal(values, req.horizon)
        baseline_mae = model_mae = None
        model_name = "naive_seasonal"
    else:
        preds, baseline_mae, model_mae = _ridge_forecast(values, req.horizon)
        model_name = "ridge_window6"

    residual_std = float(np.std(np.diff(values))) if len(values) > 2 else 5.0
    points = []
    for i, value in enumerate(preds):
        t = last_t + step * (i + 1)
        points.append(
            ForecastPoint(
                t=t,
                value=round(float(value), 2),
                model=model_name,
                lower=round(float(value - 1.28 * residual_std), 2),
                upper=round(float(value + 1.28 * residual_std), 2),
            )
        )

    return ForecastResponse(
        segment_id=req.segment_id,
        model=model_name,
        baseline_mae=baseline_mae,
        model_mae=model_mae,
        notes="Predictions derived from provided history (usually SIMULATED). Not municipal forecasts.",
        predictions=points,
    )


@app.post("/v1/anomalies", response_model=AnomalyResponse)
def anomalies(req: AnomalyRequest):
    if len(req.history) < 8:
        raise HTTPException(400, "Need at least 8 history points for anomaly detection")

    values = _values(req.history).reshape(-1, 1)
    clf = IsolationForest(contamination=req.contamination, random_state=42)
    labels = clf.fit_predict(values)
    scores = clf.decision_function(values)

    points = [
        AnomalyPoint(
            t=req.history[i].t,
            value=req.history[i].value,
            score=round(float(scores[i]), 4),
            is_anomaly=bool(labels[i] == -1),
        )
        for i in range(len(req.history))
    ]
    return AnomalyResponse(
        segment_id=req.segment_id,
        method="isolation_forest",
        anomaly_count=sum(1 for p in points if p.is_anomaly),
        notes="Anomaly flags are model outputs on the supplied series — not confirmed real incidents.",
        points=points,
    )
