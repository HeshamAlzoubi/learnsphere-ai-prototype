# Milestone 13 Gate E — JWT audience and learner self-service

**Scope:** Read-only synthetic learner attempt history, disabled by default.

## Configuration
To enable optional JWT authentication, configure:
- `learnsphere.identity.enabled=true`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri` — HTTPS issuer URI of a trusted identity provider
- `learnsphere.identity.audience` — exact audience identifier issued for LearnSphere AI

The JWT decoder validates signature, expiry and issuer using Spring Security's issuer validator and rejects tokens whose `aud` array does not contain the configured audience. Missing issuer or audience configuration fails closed when the decoder is initialized. In default mode, authentication stays disabled and the synthetic public demo remains unchanged.

## Endpoint
`GET /api/v1/learner/me/attempts` requires a validated bearer token with the trusted LEARNER role. The endpoint accepts **no caller-provided learner or organization identifier**. It resolves the unique issuer/subject binding and returns only that learner's synthetic assessment attempts. Unknown bindings and wrong issuers are rejected. Educator and admin roles do not automatically grant access.

## Testing and limitations
MockMvc tests exercise anonymous, educator, own-learner, unknown-subject, wrong-issuer and direct cross-learner URL requests. These tests inject synthetic JWT security contexts, **not** cryptographically signed bearer tokens; they do not independently prove production issuer, signature, or audience verification. End-to-end JWT tests against a trusted issuer are required before deployment.

No registration, password handling, real student records, educator roster access, or real learner onboarding is implemented. The demo endpoints are still publicly accessible on local-only binding; do not expose them publicly. A production release requires identity-provider configuration review, verified issuer and audience, key rotation and token-lifetime policies, logging/privacy review, and explicit institution approval.
