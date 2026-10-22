# LearnSphere AI — Increment 09 Identity and RBAC Design

## Status and scope
This increment implements a **deny-by-default HTTP access boundary** using Spring Security. It does **not** implement learner identity, login, sessions, OIDC, RBAC permissions for real users, or private learner progress. Only synthetic demonstration content is permitted. The existing shared demo-progress endpoint remains readable by anyone who can reach the local server. Never expose this prototype publicly or use real learner information.

## Current allowlist
- GET `/api/v1/lessons` and `/api/v1/lessons/**`
- GET `/api/v1/assessments/{assessmentId}`
- GET `/api/v1/demo-progress`
- POST `/api/v1/assessments/{assessmentId}/submissions`
- Everything else: denied (HTTP 403 expected). No authentication is attempted.

## Future identity architecture — design only
Use a mature OpenID Connect identity provider. Validate signed access tokens (issuer, audience, expiry, signature), map stable subject identifiers to internal learner records, and enforce per-resource ownership. Define roles `LEARNER`, `EDUCATOR`, `ADMIN`, with educator permissions additionally scoped to verified class membership. Require deny-by-default authorization at both HTTP and service/data access layers; role checks alone are insufficient. Avoid assigning historic shared demo attempts to learner accounts.

## Proposed permission matrix (not implemented)
| Resource/action | Learner | Educator | Admin |
|---|---|---|---|
| View own progress | Own only | No | No by default |
| Submit own assessment | Own only | No | No |
| View assigned class aggregate | No | Assigned classes only | Policy-controlled |
| Manage role assignments | No | No | Restricted, audited |

## Before implementing accounts
1. Define identity-provider and token lifecycle, logout and session strategy, CSRF policy, CORS and secure cookie policy where applicable.
2. Create immutable learner IDs and ownership-linked attempts; migrate schema with a new versioned Flyway migration.
3. Enforce class membership and row-level ownership on every read/write, with negative tests for horizontal privilege escalation.
4. Add rate limiting, audit logging, privacy review (including COPPA/FERPA as applicable), consent, retention and deletion flows.
5. Obtain real Maven/React/MySQL test results, dependency security scans, and independent security review before any real learners.

## Security caveats
The demo API deliberately permits anonymous POST submissions. CSRF is disabled only because this prototype has no cookie-based login and is restricted to synthetic local-only use. This is not a production configuration. Spring Security default headers may coexist with the prior demo header filter; verify actual HTTP responses in a full runtime test.
