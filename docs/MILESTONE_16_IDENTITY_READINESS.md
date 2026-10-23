# Milestone 16 — Trusted identity integration readiness

**Status:** Prototype integration contract and security tests. No live identity provider is connected.

## Scope and trust boundary

LearnSphere AI's synthetic demo routes remain publicly accessible for local demonstration. Private learner history is a separate, read-only API protected by Spring Security's JWT resource-server mode. Identity integration is disabled by default (`learnsphere.identity.enabled=false`). Enabling it without a real trusted issuer, expected audience, appropriate role claims and learner-subject binding is **not** a production-ready sign-in flow.

### Configuration contract

A deployment integrator must configure all of:

- `learnsphere.identity.enabled=true`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`: a verified trusted OIDC issuer, with reachable metadata/JWKS
- `learnsphere.identity.audience`: the expected API audience, **not** a frontend client ID unless the provider explicitly issues that audience
- JWT role claim `roles`: explicitly trusted `LEARNER`, `EDUCATOR`, or `ADMIN` strings mapped to Spring roles by the existing policy
- Verified subject-to-learner binding for private records; an otherwise valid JWT does not grant access to unrelated learner data

Never embed client secrets, access tokens, or signing keys in source control. No access token is accepted from URL parameters or editable UI fields. The frontend currently has no provider-backed sign-in; the app is signed out by default.

### Security acceptance tests

`SignedJwtSecurityFilterTest` exercises the complete Spring HTTP filter chain with locally generated signed JWTs and test-only public keys. Cases include valid learner identity, wrong audience, wrong issuer, expired tokens, untrusted signatures, missing or disallowed roles, unauthenticated account/history access, educator isolation from learner history, and denial when no subject-to-learner binding exists. Private history authorization denials are checked for `Cache-Control: no-store`.

These are local automated security tests; they do not constitute third-party provider validation, penetration testing, or production security certification.

## Production dependencies (not delivered in Milestone 16)

1. Select and configure a real OIDC identity provider with documented issuer, API audience, claims and key rotation.
2. Implement authorization-code + PKCE sign-in in the browser via a trusted maintained SDK, with in-memory token handling and logout/session expiry behavior.
3. Define institution-approved learner provisioning and subject-to-learner binding. Do not use email alone as an authorization key.
4. Test actual provider tokens, role provisioning, refresh/expiry, logout, and cross-learner access denial in a non-production environment.
5. Conduct privacy, security, accessibility, and operational reviews before handling real learner information.

## Verification and evidence

Review the Milestone 16 PR and its CI run history in GitHub. Do not infer production readiness from a green test suite. No real student data is used.
