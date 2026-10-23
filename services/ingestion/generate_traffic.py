#!/usr/bin/env python3
"""Deterministic synthetic traffic event generator for Urban Pulse M1.

All events are labeled sourceKind=SIMULATED. This is not real city telemetry.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import math
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime, timezone
from typing import Any
from uuid import uuid4

SEGMENTS = [
    "seg-ave-north",
    "seg-ave-mid",
    "seg-ave-south",
    "seg-st-west",
    "seg-st-center",
    "seg-st-east",
    "seg-ring-ne",
    "seg-ring-sw",
]


def seeded_unit(seed: str) -> float:
    digest = hashlib.sha256(seed.encode("utf-8")).hexdigest()
    return int(digest[:8], 16) / 0xFFFFFFFF


def build_event(
    segment: str,
    measurement_name: str,
    value: float,
    unit: str,
    source_id: str,
) -> dict[str, Any]:
    return {
        "eventId": str(uuid4()),
        "eventType": "traffic.measurement",
        "eventTimestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "sourceId": source_id,
        "spatialEntityId": segment,
        "schemaVersion": "1.0.0",
        "measurementName": measurement_name,
        "measurementValue": round(value, 2),
        "measurementUnit": unit,
        "qualityStatus": "ok",
        "sourceKind": "SIMULATED",
    }


def post_event(api_base: str, payload: dict[str, Any]) -> None:
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(
        f"{api_base.rstrip('/')}/api/v1/events",
        data=data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=10) as resp:
        if resp.status not in (200, 201):
            raise RuntimeError(f"Unexpected status {resp.status}")


def main() -> int:
    parser = argparse.ArgumentParser(description="Urban Pulse synthetic traffic generator")
    parser.add_argument("--api", default="http://127.0.0.1:3847")
    parser.add_argument("--rounds", type=int, default=3, help="Rounds over all segments")
    parser.add_argument("--delay", type=float, default=0.05)
    parser.add_argument("--seed", default="harbor-demo")
    parser.add_argument("--source-id", default="sim-generator-1")
    args = parser.parse_args()

    sent = 0
    for round_idx in range(args.rounds):
        for segment in SEGMENTS:
            phase = seeded_unit(f"{args.seed}:{segment}:{round_idx}")
            # Peak-ish pattern: lower speed + higher volume near "rush"
            rush = 0.5 + 0.5 * math.sin((round_idx + phase) * 1.3)
            speed = 12 + (50 * (1.0 - rush * 0.7)) * (0.6 + 0.4 * phase)
            vehicles = 4 + rush * 28 * (0.5 + phase)

            for name, value, unit in (
                ("speed_kmh", speed, "km/h"),
                ("vehicle_count", vehicles, "count"),
            ):
                event = build_event(segment, name, value, unit, args.source_id)
                try:
                    post_event(args.api, event)
                    sent += 1
                except urllib.error.URLError as exc:
                    print(f"API unreachable at {args.api}: {exc}", file=sys.stderr)
                    return 1
                time.sleep(args.delay)

    print(f"Sent {sent} SIMULATED events to {args.api}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
