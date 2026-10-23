# M1 — Vertical slice

## Goal

Prove an end-to-end path: generate → ingest → store/aggregate → API → UI/3D.

## Deliberate non-goals

- Real Kafka / Spark
- Real geospatial city meshes
- Auth, CV, C++ engine, forecasting

## Data mode

`SIMULATED_DEMO` — procedural Harbor District segments seeded at API boot.

## Event schema

See `data-platform/schemas/urban-event-v1.json`.
