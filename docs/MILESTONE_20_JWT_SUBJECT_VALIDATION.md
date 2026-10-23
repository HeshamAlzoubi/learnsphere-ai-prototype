# Milestone 20 — JWT Subject Validation

The optional JWT resource-server validator now rejects signed tokens without a nonblank subject (`sub`), in addition to existing signature, issuer, audience, and expiry checks. A valid role does not bypass the subject requirement.

Full Spring HTTP regression tests sign synthetic tokens with absent and whitespace-only subjects and verify unauthorized responses. Account response cache controls remain in place.

This is a security hardening increment for the prototype. It does not connect a real identity provider, provision learners, or establish production readiness. Verification is recorded in the pull request's GitHub Actions checks.
