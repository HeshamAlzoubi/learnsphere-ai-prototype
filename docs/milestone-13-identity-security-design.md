# Milestone 13 — Learner identity and progress isolation design

**Status:** Architecture proposal; no accounts or identified learner data are enabled.

## Current baseline
- All existing assessment attempts are synthetic, shared demonstration records.
- `/api/v1/demo-progress` is a shared demo view, not a learner record.
- `DemoAccessPolicy` allows only specific demo endpoints; all other routes are denied.
- Backend binds to loopback by default; this does not constitute authentication.

## Proposed domain boundaries
1. **Organization** — a school or pilot tenant, identified by a generated opaque ID.
2. **Educator** — adult identity with membership and explicit permissions scoped to an organization.
3. **Learner** — pseudonymous account ID, organization membership, and optional guardian linkage; no name, birth date, or email required for the initial prototype.
4. **Enrollment** — educator-controlled learner-to-classroom relationship, scoped to the same organization.
5. **Learner assessment attempt** — references learner ID, assessment ID, answers, score and timestamp. Kept separate from `demo_assessment_attempts`; no automatic migration of synthetic demo attempts.
6. **Access grant** — relationship-based authorization to view progress, not merely knowledge of a learner ID.

## Security requirements before any learner API is enabled
- Establish an authenticated principal from a trusted server-side identity provider or securely managed session.
- Require a verified adult educator role for roster management; prohibit self-asserted roles.
- Scope every learner progress query by both authenticated identity and authorized organization/class membership.
- Check object-level access on read, create, update, and delete operations; deny access by default.
- Use opaque identifiers; never trust client-supplied owner or tenant IDs to establish authorization.
- Add rate limiting, audit logging without sensitive answer payloads, session expiration and CSRF controls appropriate to the selected authentication mechanism.
- Protect records in transit and at rest; manage credentials outside the repository.
- Define data minimization, retention, deletion, guardian/educator consent and institutional approval before real children participate.
- Review COPPA, FERPA, applicable state requirements, and institutional policies with qualified stakeholders before collecting actual student data.

## Rollout gates
**Gate A (this increment):** document boundaries and regression-test that hypothetical identity/learner/educator/admin routes remain inaccessible.

**Gate B:** implement server-verified authentication and authorization with synthetic identities and negative cross-tenant tests. No public registration.

**Gate C:** add dedicated learner tables with foreign keys, scoped repositories and deletion/retention procedures; test isolation using synthetic accounts only.

**Gate D:** implement learner-facing progress and educator-authorized views with end-to-end tests, accessibility checks and security review.

**Gate E:** obtain required privacy, security and institutional approvals before any real student information is processed.

## Explicit non-goals for Gate A
No real accounts, passwords, learner records, registration forms, deployment exposure or claims of COPPA/FERPA compliance.
