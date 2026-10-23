# Urban Pulse Intelligence

Research-oriented **smart city intelligence** platform (portfolio / PFE track).

**Current milestone: M1 — working vertical slice**

Synthetic traffic events → Java 21 Spring Boot API (validate + aggregate) → React + Three.js digital twin console.

> All M1 measurements are **SIMULATED** on a **procedural** Harbor District. They are not municipal telemetry.

## Architecture (M1)

```text
Python generator  --HTTP JSON schema v1-->  Spring Boot API (H2)
                                              | aggregates 5-min windows
React + R3F console  <--REST analytics-----/
```

Kafka, PostGIS, Spark, forecasting, computer vision, and the C++ engine are planned for later milestones (see `docs/architecture/`).

## Stack

| Layer | Tech |
|-------|------|
| Web | React, TypeScript, Vite, Tailwind, React Three Fiber, TanStack Query |
| API | Java 21, Spring Boot, JPA, H2, Validation, Actuator |
| Ingestion | Python 3 (stdlib only) |
| Optional later | PostgreSQL/PostGIS, Redpanda/Kafka (`docker-compose.yml`) |

## Quick start

Requirements: **Java 21**, **Node 20+**, **Python 3.10+**. Docker is optional for M1.

```bash
# Terminal 1 — API
cd services/api-java && ./mvnw spring-boot:run
# listens on http://127.0.0.1:3847

# Terminal 2 — seed simulated traffic
python3 services/ingestion/generate_traffic.py --rounds 4

# Terminal 3 — web console
cd apps/web && npm install && npm run dev
# http://127.0.0.1:5178
```

Or use the Makefile: `make api` · `make ingest` · `make web`.

## Tests

```bash
cd services/api-java && ./mvnw test
```

## Key API routes

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/status` | Milestone, counts, data-mode disclaimer |
| GET | `/api/v1/city/entities` | Procedural road segments |
| GET | `/api/v1/analytics/traffic/latest` | Latest window per segment |
| POST | `/api/v1/events` | Ingest schema v1 urban events |

## Provenance

Every event carries `sourceKind`: `SIMULATED` | `OBSERVED` | `PREDICTED`.  
The UI and status endpoint surface this explicitly.

## Repository layout

```text
apps/web/
services/api-java/
services/ingestion/
data-platform/schemas/
docs/architecture/
infrastructure/docker/
docker-compose.yml
Makefile
```

## Author

Douae El Assal — ENSA Tétouan · [GitHub](https://github.com/anadouae) · [LinkedIn](https://www.linkedin.com/in/douae-el-assal)

## License

MIT (code). Datasets/models added later must keep their own licenses documented in `docs/`.
