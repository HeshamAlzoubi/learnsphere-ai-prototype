# Milestone 18 — Default-deny identity verification

This increment adds automated Spring HTTP tests to verify that account and learner-history routes remain forbidden while identity integration is disabled by default. Arbitrary bearer credentials cannot open these routes. Shared synthetic demo progress remains accessible.

The tests verify non-cacheable protected responses. No real identity provider, student account, or personal learner record is introduced.

Verification evidence is recorded in the associated pull request's GitHub Actions checks.
