# Urban Pulse Intelligence

Portfolio-grade **smart city intelligence** platform: simulated urban streaming → Java API → ML forecasts/anomalies → interactive 3D console.

**Current milestone: M2 (expanded vertical product)**

> Harbor District is a **procedural demo city**. Traffic and air-quality values are **SIMULATED** unless otherwise labeled. Forecasts/anomalies are **PREDICTED**.

## What works now

- Synthetic traffic ingestion (schema v1) + 5-minute aggregates
- Seeded historical windows for immediate forecasting demos
- Java 21 / Spring Boot API (traffic, environment, scenarios, ML proxy)
- Python FastAPI ML service (Ridge forecast + IsolationForest)
- React console: Overview · 3D Twin · Traffic · Environment · Forecast · Scenarios · Pipeline
- Provenance labels everywhere · CI for API + web

## Stack

| Layer | Tech |
|-------|------|
| Web | React, TypeScript, Vite, Tailwind, R3F, Recharts, React Router |
| API | Java 21, Spring Boot 3.4, JPA, H2, Validation, Actuator |
| ML | FastAPI, scikit-learn, NumPy, pandas |
| Ingestion | Python 3 |
| Later | Kafka/Redpanda, PostGIS (`docker-compose.yml`) |

## Quick start

Needs Java 21, Node 20+, Python 3.10+.

```bash
# Terminal 1 — API
cd services/api-java && ./mvnw spring-boot:run
# :3847

# Terminal 2 — ML
cd services/ml-inference
pip install -r requirements.txt
python3 -m uvicorn app:app --host 127.0.0.1 --port 3851

# Terminal 3 — optional extra live events
python3 services/ingestion/generate_traffic.py --rounds 4

# Terminal 4 — web
cd apps/web && npm install && npm run dev
# http://127.0.0.1:5178
```

Or: `make api` · `make ml` · `make ingest` · `make web`.

## Tests

```bash
cd services/api-java && ./mvnw test
cd apps/web && npm run build
```

## Clone on your Windows Desktop

```powershell
cd C:\Users\daurinia\Desktop
git clone https://github.com/anadouae/urban-pulse-intelligence.git
```

(The Cloud agent cannot write to your Windows Desktop; GitHub is the source of truth.)

## Author

Douae El Assal — ENSA Tétouan · [@anadouae](https://github.com/anadouae) · [LinkedIn](https://www.linkedin.com/in/douae-el-assal)

## License

MIT (code). Document dataset/model licenses separately when added.
