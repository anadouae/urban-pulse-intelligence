# Security — Urban Pulse Intelligence

Portfolio / PFE project. Harbor District data is **SIMULATED**. This document states what is enforced today and what is intentionally out of scope.

## Enforced now

| Control | Detail |
|---------|--------|
| Input validation | Jakarta Validation on ingest + scenarios |
| CORS | Restricted to local Vite origins (`WebConfig`) |
| API key | Optional `X-API-Key` on mutating `/api/**` when `URBAN_PULSE_API_KEY` is set (or `URBAN_PULSE_API_KEY_REQUIRED=true`) |
| Security headers | `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Cache-Control` |
| Actuator | Exposed: `health`, `info`, `metrics` only |
| Secrets | `.env` gitignored; use `.env.example` |
| H2 console | On by default for local demo; **off** under `postgres` profile / `URBAN_PULSE_H2_CONSOLE=false` |
| Provenance | Events/forecasts labeled SIMULATED / PREDICTED |

## Local demo vs hardened demo

```bash
# Open local (default) — OK for learning on localhost only
./mvnw spring-boot:run

# Hardened demo
export URBAN_PULSE_API_KEY='replace-me'
export URBAN_PULSE_API_KEY_REQUIRED=true
export URBAN_PULSE_H2_CONSOLE=false
./mvnw spring-boot:run
# Then: curl -H "X-API-Key: replace-me" -X POST ...
```

Postgres profile (no H2 console):

```bash
docker compose --profile infra up -d postgres
export SPRING_PROFILES_ACTIVE=postgres
export URBAN_PULSE_API_KEY='replace-me'
./mvnw spring-boot:run
```

## Out of scope (not claimed)

- OAuth2 / OIDC / JWT user accounts  
- mTLS between services  
- WAF / cloud IAM  
- Running Kubernetes cluster hardening  
- Real municipal camera PII  

## Reporting

Portfolio project under [@anadouae](https://github.com/anadouae). Treat findings as educational unless a real secret was committed (rotate immediately).
