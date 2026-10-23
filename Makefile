.PHONY: api test-api ingest web install-web demo status

API_DIR=services/api-java
WEB_DIR=apps/web
API_URL?=http://127.0.0.1:3847

install-web:
	cd $(WEB_DIR) && npm install

api:
	cd $(API_DIR) && ./mvnw spring-boot:run

test-api:
	cd $(API_DIR) && ./mvnw test

ingest:
	python3 services/ingestion/generate_traffic.py --api $(API_URL) --rounds 4

web:
	cd $(WEB_DIR) && npm run dev -- --host 127.0.0.1 --port 5178

status:
	curl -s $(API_URL)/api/v1/status | python3 -m json.tool

demo: ## remind operators of the demo order
	@echo "1) make api"
	@echo "2) make ingest"
	@echo "3) make web  -> http://127.0.0.1:5178"
