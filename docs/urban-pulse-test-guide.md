# Urban Pulse — test guide (live + Windows)

**Try Live (Cloud desktop):** [Try Live](bc-ef6558ee-1a03-54d0-b5f7-24d58caeb1c0#desktop)

## Live URLs on this VM (Africa/Casablanca session)

| Service | URL |
|---------|-----|
| **Web console** | http://127.0.0.1:5178 |
| **Java API** | http://127.0.0.1:3847 |
| **API health** | http://127.0.0.1:3847/actuator/health |
| **API status** | http://127.0.0.1:3847/api/v1/status |
| **ML inference** | http://127.0.0.1:3851 |
| **ML OpenAPI UI** | http://127.0.0.1:3851/docs |
| **Kafka** | `127.0.0.1:9092` (broker up when started) |

Data mode: **SIMULATED_DEMO**. Forecasts are **PREDICTED**. Persistence is **H2 in-memory** (resets when the API process restarts).

OpenAPI + Postman also live in the repo:

- `docs/openapi/urban-pulse-api.v1.yaml`
- `docs/postman/Urban-Pulse-API.postman_collection.json`

---

## Quick curl (main routes)

```bash
# Health + status
curl -s http://127.0.0.1:3847/actuator/health
curl -s http://127.0.0.1:3847/api/v1/status | python -m json.tool

# City graph + latest traffic
curl -s http://127.0.0.1:3847/api/v1/city/entities | python -m json.tool | head
curl -s http://127.0.0.1:3847/api/v1/analytics/traffic/latest | python -m json.tool | head

# History for one segment
curl -s http://127.0.0.1:3847/api/v1/analytics/traffic/seg-ave-mid/history | python -m json.tool | head

# Environment + pipeline (Kafka counters)
curl -s http://127.0.0.1:3847/api/v1/environment/latest | python -m json.tool
curl -s http://127.0.0.1:3847/api/v1/pipeline/status | python -m json.tool

# Ingest one SIMULATED event
curl -s -X POST http://127.0.0.1:3847/api/v1/events \
  -H 'Content-Type: application/json' \
  -d '{
    "eventId": "demo-evt-001",
    "eventType": "traffic.measurement",
    "eventTimestamp": "2026-10-24T10:00:00Z",
    "sourceId": "curl-demo",
    "spatialEntityId": "seg-ave-mid",
    "schemaVersion": "1.0.0",
    "measurementName": "avg_speed_kmh",
    "measurementValue": 42.5,
    "measurementUnit": "km/h",
    "qualityStatus": "ok",
    "sourceKind": "SIMULATED"
  }'

# Scenarios
curl -s http://127.0.0.1:3847/api/v1/scenarios | python -m json.tool
curl -s -X POST http://127.0.0.1:3847/api/v1/scenarios \
  -H 'Content-Type: application/json' \
  -d '{"name":"Rush stress","assumptions":"1.5x demand","demandMultiplier":1.5,"capacityMultiplier":1.0,"incidentMinutes":15}'

# ML via Java proxy (needs history in API)
curl -s -X POST http://127.0.0.1:3847/api/v1/ml/forecast/seg-ave-mid
curl -s -X POST http://127.0.0.1:3847/api/v1/ml/anomalies/seg-ave-mid

# ML direct
curl -s http://127.0.0.1:3851/health
```

Import the Postman collection and set `baseUrl` = `http://127.0.0.1:3847`.

---

## Clone + run on Windows PC

```powershell
cd C:\Users\daurinia\Desktop
git clone https://github.com/anadouae/urban-pulse-intelligence.git
cd urban-pulse-intelligence
```

**Prereqs:** Java 21, Node 20+, Python 3.10+, (optional) Docker Desktop for compose/Kafka.

### Minimal (no Kafka, HTTP ingest only)

```powershell
# Terminal 1 — API (set env if you want Kafka off)
$env:URBAN_PULSE_KAFKA_ENABLED="false"
cd services\api-java
.\mvnw.cmd spring-boot:run

# Terminal 2 — ML
cd services\ml-inference
pip install -r requirements.txt
python -m uvicorn app:app --host 127.0.0.1 --port 3851

# Terminal 3 — synthetic events over HTTP
python services\ingestion\generate_traffic.py --api http://127.0.0.1:3847 --rounds 4

# Terminal 4 — web
cd apps\web
npm install
npm run dev
# http://127.0.0.1:5178
```

### With Docker Compose (infra + apps)

When Docker Desktop is running:

```powershell
docker compose up --build
```

See `docker-compose.yml` for Postgres/Redpanda + api/ml/web services.  
**Note:** default Java config still uses **H2** unless you change datasource to the compose Postgres URL — that switch is documented as a next step, not done by default.

### Postman on Windows

1. Install Postman.  
2. Import `docs/postman/Urban-Pulse-API.postman_collection.json`.  
3. Variable `baseUrl` → `http://127.0.0.1:3847`.  
4. Run folder “Smoke”.

Or open `docs/openapi/urban-pulse-api.v1.yaml` in Swagger Editor / Insomnia / Bruno.

---

## What to click in the UI

1. **Overview** — status, event counts, SIMULATED label.  
2. **3D Twin** — Harbor District segments.  
3. **Traffic** — latest aggregates.  
4. **Forecast / Scenarios** — PREDICTED outputs.  
5. **Pipeline** — Kafka enabled + consumed counters when broker is up.
