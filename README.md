# LearnSphere AI — Middle-school learning prototype

LearnSphere AI is a developing educational prototype for grades 6–8. The repository contains a Java 21 / Spring Boot backend, React / TypeScript frontend, MySQL migration support, and automated verification. It is **not a deployed student-facing service** and must not be used with real student data.

## Run and verify

- Backend: `cd backend && mvn -B clean verify` (JDK 21; local MySQL configuration required for runtime).
- Frontend: `cd frontend && npm ci && npm run typecheck && npm test && npm run build`.
- Browser E2E and MySQL integration run in GitHub Actions workflows; see `.github/workflows/`.
- Core-only JDK check: `python tools/verify_core.py` (does not verify Spring Boot, MySQL, or React).
- Local backend binds to `127.0.0.1:8080` by default; Vite frontend uses `127.0.0.1:5173` with an API proxy.

## Current security boundary (Milestones 14–15)

- The **shared demo** is not a learner identity. Demo progress is not private learner history.
- The learner-history screen is signed-out by default. A trusted identity provider is **not yet integrated**; no token-entry UI, localStorage token storage, or production login is provided.
- Protected learner history requests accept a token only from the application's trusted caller and use `Authorization: Bearer`, cancellation signals, and `cache: 'no-store'`.
- Session changes cancel stale history requests and prevent old responses from being displayed.
- The backend's `PrivateLearnerCacheFilter` applies `Cache-Control: no-store` and `Pragma: no-cache` to `/api/v1/learner` responses, including protected history.
- These controls are defense-in-depth, **not** proof of production authentication, deployment readiness, or FERPA compliance.

## Milestone verification

- Milestone 14: learner dashboard separation, session-scoped history and stale-request cancellation; see merged pull requests and CI records.
- Milestone 15: Gate A cancellation error isolation; Gate B frontend no-store requests; Gate C server-side no-store responses; Gate D documentation and closeout.
- Pull requests must pass applicable automated checks before merging. CI passing does not replace security review, real identity-provider configuration, or deployment testing.

## Next dependencies

A trusted identity provider, configured JWT issuer/audience and role mappings, end-to-end authenticated sessions, secure hosting, operational monitoring, accessibility review, and student-data governance require separate implementation and verification before any real learner rollout.

## Milestone 16 — identity readiness

The signed-JWT authorization regression suite and the trusted-provider integration contract are documented in [Milestone 16 identity readiness](docs/MILESTONE_16_IDENTITY_READINESS.md). No live sign-in provider is connected; production identity and learner provisioning remain separate dependencies.

## Milestone 17 — private account cache protection

Protected account identity responses now receive the same server-side no-store cache headers as private learner history. See [Milestone 17 verification notes](docs/MILESTONE_17_ACCOUNT_CACHE_PROTECTION.md). This does not introduce live sign-in or real learner records.
