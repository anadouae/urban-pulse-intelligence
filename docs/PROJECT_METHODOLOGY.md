# Méthodologie projet — Urban Pulse

Aligné sur un cycle d’ingénierie informatique (PFE / portfolio), pas un tutoriel jetable.

## 1. Cadrage

| Élément | Contenu |
|---------|---------|
| Problème | Intelligence urbaine temps (quasi) réel sur trafic simulé |
| Périmètre | Ingestion → API → agrégats → ML → console 3D |
| Hors périmètre | Données municipales réelles, auth utilisateurs finaux, cluster prod |
| Critères de succès | API testée, démo navigateur, provenance claire, artefacts Docker/CI/OpenAPI |

## 2. Architecture

Voir `docs/architecture/m1-vertical-slice.md` et le README. Flux :

```text
[Ingestion HTTP/Kafka] → [Spring API + JPA] → [Analytics]
                              ↓
                        [ML FastAPI]
                              ↓
                        [React console]
```

## 3. Réalisation

1. Schéma événement (`data-platform/schemas`)  
2. API + persistance (H2 défaut / Postgres profil)  
3. ML baseline + proxy  
4. UI multi-pages + labels provenance  
5. Kafka optionnel  
6. Qualité : tests JUnit, CI, OpenAPI/Postman, Docker, manifests K8s  

## 4. Vérification

- `./mvnw test` · `npm run build` · collection Postman · guide `docs/urban-pulse-test-guide.md`  
- Health: `/actuator/health` · Swagger: `/swagger-ui.html`  

## 5. Sécurité & exploitation

Voir `SECURITY.md`. Pas de secrets dans git. API key pour démos exposées.

## 6. Livrables portfolio

Code GitHub `anadouae/urban-pulse-intelligence`, README, OpenAPI, Docker, K8s YAML (intent), CI badge via Actions.
