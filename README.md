# LearnSphere AI — Increment 11 (verification hardening)

This package includes the full Increment 10 source plus a narrow correctness fix for `AssessmentResult` and a dependency-free JDK verification script.

## Run now (no Maven required)

From the project root, run `python tools/verify_core.py` with JDK 21 on PATH. This verifies the scoring core only; it does **not** compile Spring Boot or validate MySQL/React.

## Full verification when tools are available

- `cd backend && mvn -B clean verify`
- `cd frontend && npm install --no-audit --no-fund && npm run typecheck && npm test && npm run build`
- Run real MySQL migration and end-to-end tests separately.

No real student data; no public deployment.
