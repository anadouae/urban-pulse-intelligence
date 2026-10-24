# Kubernetes manifests (Urban Pulse)

Minimal **Deployment + Service** resources for `api`, `ml`, and `web`.

## Honesty

- These YAML files are **real manifests** suitable for a local cluster (kind / minikube / Docker Desktop Kubernetes).
- They are **not applied** on the Cloud agent VM (no `kubectl` cluster here).
- Images must be built and tagged locally first, e.g.:

```bash
docker build -t urban-pulse-api:local services/api-java
docker build -t urban-pulse-ml:local services/ml-inference
docker build -t urban-pulse-web:local apps/web
kubectl apply -f infrastructure/k8s/namespace.yaml
kubectl apply -f infrastructure/k8s/
kubectl -n urban-pulse port-forward svc/web 8080:80
```

- Default API uses **H2 in-memory** and Kafka disabled in-cluster unless you extend ConfigMaps/Secrets.
- Service DNS names (`api`, `ml`, `web`) match `docker-compose` and `apps/web/nginx.conf`.
