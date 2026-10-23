# Milestone 13 Gate F — Signed JWT security verification

**Status:** CI security test increment; synthetic keys and identities only.

## Coverage
A dedicated Spring Boot MockMvc test suite creates ephemeral RSA key pairs, signs actual RS256 JWTs, and submits them as HTTP Bearer tokens through the application's Spring Security filter chain. The test-only decoder trusts one generated RSA public key and uses the **same issuer/audience/expiry validators** as the production decoder.

Checks cover a valid signed token, wrong audience, wrong issuer, expired token, signature from an untrusted key, incorrect role, and an authenticated learner with no database identity binding. The latter remains forbidden.

## Boundaries
- The RSA private keys are generated only in tests and are never deployed or committed.
- Production decoder still uses the configured trusted issuer and its published signing keys; tests do **not** establish a live identity-provider integration, key-rotation resilience, or operational readiness.
- Authentication remains opt-in and disabled by default. Synthetic demo routes remain public only for local demonstration.
- No real learner identities, registrations, passwords, or production data are added.
- Deployment requires approved issuer/audience settings, TLS, a privacy and threat-model review, and live issuer/JWKS integration testing.

## Acceptance
Review the backend tests, MySQL integration, HTTP application smoke test, and browser E2E test results on the pull request and again after merging. Do not merge on red checks.
