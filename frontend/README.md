# LearnSphere AI — Prototype Increment 05 frontend

**Status:** source prepared; local frontend build/tests are separately recorded in the verification report. Full backend-to-frontend execution is not yet verified.

## Requirements
- Node.js 20+ and npm
- Java 21, Maven, MySQL (for running the existing backend)

## Start backend
From `../backend`, configure MySQL per its README, then `mvn clean test` and `mvn spring-boot:run`. This requires a running MySQL instance and Flyway migrations.

## Start frontend
From `frontend`, run `npm ci` (or `npm install` if no lockfile), then `npm run dev`. Visit http://127.0.0.1:5173. Vite proxies `/api` requests to http://127.0.0.1:8080 so no CORS modification is required for local development.

## Quality checks
Run `npm run typecheck`, `npm test`, `npm run build`. No real student data or credentials. Do not publish this prototype as a production application.

## Contract / scope
The frontend consumes GET `/api/v1/lessons`, GET `/api/v1/lessons/{id}`, GET `/api/v1/assessments/{id}`, POST `/api/v1/assessments/{id}/submissions`.

The backend currently has only `lesson-001` and `assessment-001`. The backend returns option IDs `a`, `b`, `c`, not display labels, so `src/content.ts` supplies **demo-only** question-specific labels. This is a temporary contract gap. The frontend never grades answers and does not embed the answer key; scoring and persistence happen on the backend. Lesson explanatory content is also demo-only.

The recommendation is rule-based, not a trained AI model. No authentication, user accounts, personal data, accessibility certification, or production security is claimed.
