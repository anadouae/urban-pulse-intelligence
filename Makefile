.PHONY: api ml ingest web test-api install-web demo status

API_DIR=services/api-java
WEB_DIR=apps/web
ML_DIR=services/ml-inference
API_URL?=http://127.0.0.1:3847
ML_URL?=http://127.0.0.1:3851
export PATH:=$(HOME)/.local/bin:/tmp/apache-maven-3.9.9/bin:$(PATH)

install-web:
	cd $(WEB_DIR) && npm install

install-ml:
	python3 -m pip install -r $(ML_DIR)/requirements.txt

api:
	cd $(API_DIR) && ./mvnw spring-boot:run

ml:
	cd $(ML_DIR) && python3 -m uvicorn app:app --host 127.0.0.1 --port 3851

test-api:
	cd $(API_DIR) && ./mvnw test

ingest:
	python3 services/ingestion/generate_traffic.py --api $(API_URL) --rounds 4

ingest-kafka:
	python3 services/ingestion/generate_traffic_kafka.py --rounds 4

kafka-topics:
	bash scripts/create-topics.sh

web:
	cd $(WEB_DIR) && npm run dev -- --host 127.0.0.1 --port 5178

status:
	@curl -s $(API_URL)/api/v1/status | python3 -m json.tool
	@curl -s $(ML_URL)/health | python3 -m json.tool

demo:
	@echo "1) make api"
	@echo "2) make ml"
	@echo "3) make ingest"
	@echo "4) make web -> http://127.0.0.1:5178"
