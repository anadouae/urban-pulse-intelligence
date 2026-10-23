#!/usr/bin/env bash
set -euo pipefail
KAFKA_HOME="${KAFKA_HOME:-$HOME/tools/kafka_2.13-3.9.1}"
export KAFKA_HEAP_OPTS="${KAFKA_HEAP_OPTS:--Xmx256M -Xms128M}"

if [[ ! -d "$KAFKA_HOME" ]]; then
  echo "Kafka not found at $KAFKA_HOME"
  echo "Download: https://archive.apache.org/dist/kafka/3.9.1/kafka_2.13-3.9.1.tgz"
  exit 1
fi

cd "$KAFKA_HOME"
if [[ ! -f /tmp/kraft-combined-logs/meta.properties ]]; then
  CLUSTER_ID=$("./bin/kafka-storage.sh" random-uuid)
  "./bin/kafka-storage.sh" format -t "$CLUSTER_ID" -c config/kraft/server.properties
fi

"./bin/kafka-server-start.sh" config/kraft/server.properties
