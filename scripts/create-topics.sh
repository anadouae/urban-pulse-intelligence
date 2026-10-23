#!/usr/bin/env bash
set -euo pipefail
KAFKA_HOME="${KAFKA_HOME:-$HOME/tools/kafka_2.13-3.9.1}"
BS="${KAFKA_BOOTSTRAP:-127.0.0.1:9092}"
"$KAFKA_HOME/bin/kafka-topics.sh" --bootstrap-server "$BS" --create --if-not-exists \
  --topic urban.traffic.events --partitions 3 --replication-factor 1
"$KAFKA_HOME/bin/kafka-topics.sh" --bootstrap-server "$BS" --list
