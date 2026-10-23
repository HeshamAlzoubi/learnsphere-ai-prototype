# Milestone 13 Gate C — Synthetic learner data storage

**Status:** Database foundation only. No student accounts, registration, or learner-data HTTP endpoints.

## Implemented database structures
Flyway migration V2 creates `learner_organizations`, `synthetic_learners`, `learner_classrooms`, `learner_enrollments`, and `learner_assessment_attempts`. Composite organization/learner and organization/classroom foreign keys prevent references to a learner or classroom belonging to another organization. Attempt scores are checked for valid ranges. The original `demo_assessment_attempts` tables remain unchanged and separate.

## Security boundary
- No real names, contact details, dates of birth, credentials, or other identifiable learner data are required by this schema.
- No learner API is opened by this increment; existing deny-by-default Spring Security routing remains in force.
- Database foreign keys enforce relationship integrity, **not** authorization. An authenticated application identity still needs organization membership and per-object access checks before any learner records may be returned or changed.
- The current JWT feature is disabled by default and lacks production audience enforcement. Do not expose these tables via new APIs until issuer/audience validation and object-level authorization are implemented and tested.
- No claim of COPPA/FERPA compliance or production readiness.

## Automated checks
H2-backed tests exercise scoped queries, rejected cross-organization enrollment/attempts and invalid scores. MySQL 8.4 integration checks V2 tables and cross-organization attempt rejection. Existing backend, browser, and HTTP smoke tests must continue passing.

## Next phase
Create tenant-scoped repositories/services with authenticated subject-to-organization membership mapping; add negative cross-tenant and horizontal-access tests. Keep all records synthetic pending institutional privacy/security review.
