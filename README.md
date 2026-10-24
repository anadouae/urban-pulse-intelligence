# Urban Pulse Intelligence

Portfolio-grade **smart city intelligence** platform: simulated urban streaming → Java API → ML forecasts/anomalies → interactive 3D console.

**Current milestone:** vertical product with optional **real Kafka** path (topic `urban.traffic.events`).

> Harbor District is a **procedural demo city**. Traffic and air-quality values are **SIMULATED** unless otherwise labeled. Forecasts/anomalies are **PREDICTED**.  
> Default persistence is **H2 in-memory** (not Postgres). Use `--spring.profiles.active=postgres` + compose `infra` for PostGIS.  
> Security: optional `URBAN_PULSE_API_KEY` / `X-API-Key` on mutating routes — see [`SECURITY.md`](SECURITY.md). Methodology: [`docs/PROJECT_METHODOLOGY.md`](docs/PROJECT_METHODOLOGY.md).  
> Kubernetes manifests under `infrastructure/k8s/` are real YAML; they are **not** applied unless you have a cluster.

## What works now

- Synthetic traffic ingestion (schema v1) + 5-minute aggregates + seeded history
- Java 21 / Spring Boot API (traffic, environment, scenarios, ML proxy, pipeline)
- Python FastAPI ML service (Ridge forecast + IsolationForest)
- React console: Overview · 3D Twin · Traffic · Environment · Forecast · Scenarios · Pipeline
- Kafka consumer when broker is up (`URBAN_PULSE_KAFKA_ENABLED=true`)
- OpenAPI + Postman collection · Dockerfiles · docker-compose app services · K8s Deployment/Service
- CI: API tests + web build (GitHub Actions)

## Stack

| Layer | Tech |
|-------|------|
| Web | React, TypeScript, Vite, Tailwind, R3F, Recharts, React Router |
| API | Java 21, Spring Boot 3.4, JPA, **H2**, Validation, Actuator, Spring Kafka |
| ML | FastAPI, scikit-learn, NumPy, pandas |
| Ingestion | Python 3 (HTTP + Kafka producers) |
| Infra (optional) | PostGIS + Redpanda via `docker compose --profile infra` |
| Deploy artifacts | Dockerfiles, compose app services, `infrastructure/k8s/` |

## Quick start (no Docker)

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

Kafka (optional, host binary or Redpanda): `make kafka-topics` then `make ingest-kafka`.

## Docker Compose

```bash
docker compose up --build
# web http://127.0.0.1:5178  api :3847  ml :3851
# optional infra:
docker compose --profile infra up -d postgres redpanda
```

Images were authored for Docker Desktop / CI. The Cloud agent VM used for demos may not have the Docker CLI.

## API docs & testing

| Artifact | Path |
|----------|------|
| OpenAPI (file) | [`docs/openapi/urban-pulse-api.v1.yaml`](docs/openapi/urban-pulse-api.v1.yaml) |
| Swagger UI (runtime) | http://127.0.0.1:3847/swagger-ui.html |
| Postman | [`docs/postman/Urban-Pulse-API.postman_collection.json`](docs/postman/Urban-Pulse-API.postman_collection.json) |
| Test guide | [`docs/urban-pulse-test-guide.md`](docs/urban-pulse-test-guide.md) |
| Security | [`SECURITY.md`](SECURITY.md) |
| Methodology | [`docs/PROJECT_METHODOLOGY.md`](docs/PROJECT_METHODOLOGY.md) |
| ML Swagger | http://127.0.0.1:3851/docs when ML is running |

```bash
curl -s http://127.0.0.1:3847/api/v1/status
curl -s http://127.0.0.1:3847/api/v1/analytics/traffic/latest | head
```

## Kubernetes

See [`infrastructure/k8s/README.md`](infrastructure/k8s/README.md). Build images, `kubectl apply -f infrastructure/k8s/`.

## Tests / CI

```bash
cd services/api-java && ./mvnw test
cd apps/web && npm run build
```

GitHub Actions: `.github/workflows/ci.yml`.

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
