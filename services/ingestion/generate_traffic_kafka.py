#!/usr/bin/env python3
"""Publish SIMULATED traffic events to Kafka topic urban.traffic.events."""

from __future__ import annotations

import argparse
import hashlib
import json
import math
import sys
import time
from datetime import datetime, timezone
from uuid import uuid4

from kafka import KafkaProducer

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


def build_event(segment: str, measurement_name: str, value: float, unit: str, source_id: str) -> dict:
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


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--bootstrap", default="127.0.0.1:9092")
    parser.add_argument("--topic", default="urban.traffic.events")
    parser.add_argument("--rounds", type=int, default=3)
    parser.add_argument("--delay", type=float, default=0.02)
    parser.add_argument("--seed", default="harbor-kafka")
    parser.add_argument("--source-id", default="sim-kafka-producer-1")
    args = parser.parse_args()

    try:
        producer = KafkaProducer(
            bootstrap_servers=args.bootstrap,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda v: v.encode("utf-8") if v else None,
            acks="all",
            retries=3,
        )
    except Exception as exc:  # noqa: BLE001
        print(f"Cannot connect to Kafka at {args.bootstrap}: {exc}", file=sys.stderr)
        return 1

    sent = 0
    for round_idx in range(args.rounds):
        for segment in SEGMENTS:
            phase = seeded_unit(f"{args.seed}:{segment}:{round_idx}")
            rush = 0.5 + 0.5 * math.sin((round_idx + phase) * 1.3)
            speed = 12 + (50 * (1.0 - rush * 0.7)) * (0.6 + 0.4 * phase)
            vehicles = 4 + rush * 28 * (0.5 + phase)
            for name, value, unit in (("speed_kmh", speed, "km/h"), ("vehicle_count", vehicles, "count")):
                event = build_event(segment, name, value, unit, args.source_id)
                producer.send(args.topic, key=segment, value=event)
                sent += 1
                time.sleep(args.delay)

    producer.flush()
    producer.close()
    print(f"Published {sent} SIMULATED events to Kafka topic {args.topic} @ {args.bootstrap}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
