# Milestone 13 Gate D — Synthetic learner ownership service

**Scope:** internal read-only service; no new learner HTTP routes and no real student data.

Flyway V3 binds a synthetic learner (organization ID and learner ID) to a unique pair of trusted identity-provider issuer and subject. The read-only `SyntheticLearnerAttemptService` accepts a Spring Security `JwtAuthenticationToken`, requires the LEARNER authority, resolves the issuer/subject pair using a parameterized database query, and reads attempts only for the resulting organization and learner. It does not accept an organization or learner identifier from a caller.

Tests cover separate organizations, missing bindings, wrong issuer and a role without learner ownership. No authenticated learner route has been exposed. Existing shared demo routes remain distinct.

**Limitations and security gates:** The service assumes the token was validated by Spring Security upstream. Current optional JWT configuration does not enforce a dedicated audience and is disabled by default. Synthetic identity bindings are inserted only by controlled test/administrative setup; there is no provisioning endpoint. This service is not a complete tenant authorization system and does not implement educator permissions, classroom membership validation, enrollment policies, account lifecycle, pagination, or privacy compliance. Before exposing learner endpoints, validate issuer and audience, add integration tests through the HTTP security filter chain, add same-organization horizontal-access tests, and conduct a security/privacy review.
