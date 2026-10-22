# Increment 10 — verification matrix

This increment is a stabilization checkpoint, **not** evidence that the application builds or runs. Static source checks are only one layer.

| Gate | Command / procedure | Pass criterion | Current status |
|---|---|---|---|
| Source contract | `python tools/static_audit.py` | All checks pass | Executed in authoring environment |
| Backend compile + unit/integration tests | `cd backend && mvn -B clean verify` | Exit 0, no test failures | NOT RUN |
| Frontend dependencies | `cd frontend && npm ci` (or `npm install` if lockfile absent) | Exit 0 | NOT RUN |
| Frontend typecheck/build/tests | `npm run typecheck && npm test && npm run build` | Exit 0 | NOT RUN |
| MySQL startup | `docker compose up -d db` (check service name in compose.yaml) | Healthy database | NOT RUN |
| End-to-end local demo | Run backend/frontend and complete all 3 lesson assessments | Results and synthetic progress displayed correctly | NOT RUN |
| Access boundary | Run `AccessBoundaryIntegrationTest` with Spring Boot | Expected permit/deny statuses | NOT RUN |

No real student records, credentials, or personally identifying learner data may be used. Do not publish these unauthenticated demo endpoints.

## Access policy refinement
The previous `GET /api/v1/lessons/**` pattern allowed every nested lesson URL. Increment 10 narrows it to the implemented list, item, and lesson-assessment routes. All other paths remain denied by default. The regression tests express the intended behavior but have **not** been executed.

## Test-environment limitations
The integration tests use an H2 datasource in MySQL compatibility mode; passing those tests alone would not establish full MySQL compatibility. A separate real MySQL migration/persistence run is required.
