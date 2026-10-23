# Milestone 17 — Private Account Response Cache Protection

## Objective
Extend defense-in-depth cache controls from private learner history to authenticated account identity responses.

## Implementation
The existing `PrivateLearnerCacheFilter` now covers both `/api/v1/learner` and `/api/v1/account` path families. Protected responses, including authentication failures, receive `Cache-Control: no-store` and `Pragma: no-cache`. Exact path-prefix matching avoids changing unrelated routes such as `/api/v1/accounting-demo` or the shared synthetic demo endpoint.

## Verification
Unit tests check account and learner response headers, unaffected public paths, and an authenticated Spring Security HTTP request to `/api/v1/account/me`, including unauthenticated rejection. The PR's GitHub Actions results are the authoritative execution evidence.

## Limitations
No live OIDC provider, learner provisioning, or real student data was added. These headers do not guarantee all intermediary cache behavior or establish production privacy compliance. Operational review remains required.
