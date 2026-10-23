# Milestone 13 Gate B — Opt-in JWT authentication boundary

**Status:** Initial authentication infrastructure; no real accounts or learner records.

## Default safety behavior
The `learnsphere.identity.enabled` flag defaults to `false`. Existing synthetic demo endpoints retain their behavior. `GET /api/v1/account/me` and all future learner, roster and registration routes remain denied when disabled.

## Enabling only with a trusted identity provider
Configure `learnsphere.identity.enabled=true` and `spring.security.oauth2.resourceserver.jwt.issuer-uri` to the HTTPS issuer URI of a trusted OpenID Connect provider. Spring Security validates JWT signatures and issuer using provider metadata and published keys. Configure and verify expected token audience, issuer trust, and key rotation **before any deployment**; this increment does not yet enforce a dedicated audience check. Do not enable the flag on an internet-exposed environment until audience validation and threat review are complete.

The resource server expects a `roles` claim containing an array of exact strings `LEARNER`, `EDUCATOR`, or `ADMIN`, issued by the trusted provider. A role cannot be self-selected by a client. Unknown roles are ignored. No access is granted to other learner APIs even with an accepted role.

`GET /api/v1/account/me` returns only the token subject and recognized roles; no personal details or progress data. Access requires one recognized role. A valid token does not authorize access to another learner's data.

## Testing and limitations
Tests use synthetic JWTs via Spring Security's MockMvc test support; they do not verify a live identity provider or actual JWT signature/issuer validation. They cover anonymous rejection, recognized role acceptance, unknown/missing roles, and denied access to unimplemented learner routes. The existing deny-by-default tests remain in place.

No user registration, password storage, individual progress, educator roster, guardian consent, or real learner data is implemented. Role membership alone will not suffice for object-level authorization; organization and enrollment membership checks are required in later gates. CSRF is disabled for stateless bearer-token APIs; revisit if cookies or browser sessions are added.

**Next gate:** enforce issuer and audience validation in an isolated environment, then add tenant-scoped synthetic learner data with negative cross-organization access tests before any actual learner onboarding.
