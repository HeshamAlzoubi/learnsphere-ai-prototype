# LearnSphere AI — Increment 08 security architecture (design baseline)

## Current state, not a security certification
The prototype has **no authentication, authorization, per-learner identity, session management, or production-grade abuse protection**. Only synthetic demonstration records are allowed. Do not expose it on a public network or process real student information.

## Implemented in Increment 08
- Backend binds to `127.0.0.1` by default (`LEARN_BIND_ADDRESS` override is provided for controlled future testing, NOT an invitation to deploy publicly).
- Response headers: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: no-referrer`, `Cache-Control: no-store`.
- Added backend regression test sources for response headers and malformed submissions.
- Added frontend API client test sources and a CI workflow for build/tests.
- Existing synthetic demonstration database and validation remain unchanged.

## Threat model (selected)
| Threat | Present exposure | Planned mitigation before real learners |
|---|---|---|
| Shared progress visibility | Anyone reaching the API can see synthetic aggregate progress | Replace with per-learner ownership checks and role-based authorization |
| Unauthorized submissions | No identity or rate limiting | Authenticated identities, CSRF/cookie policy, rate limits, audit logs |
| Data privacy | No real personal data permitted | Data minimization, parental/school consent assessment, retention/deletion workflows |
| Insecure deployment | Prototype not hardened | HTTPS, secure secrets, dependency scanning, security review, least-privilege DB |
| Answer disclosure | Public DTO omits correct answer key | Add automated schema tests and assessment-integrity review |

## Future account design — NOT IMPLEMENTED
Roles: learner, educator, administrator. Identity should be provided by a well-reviewed OIDC provider or equivalent, not homemade password handling. Progress records require immutable learner ownership identifiers, access-control checks at every read/write, and educator access scoped to assigned classes. Migration from shared synthetic progress must not relabel old demo attempts as learner records.

## Gate for handling real students
Written privacy/legal review (including relevant COPPA/FERPA obligations), access-control tests, threat modeling, deployment hardening, security monitoring, data retention, consent processes where required, and end-to-end verification are prerequisites.
