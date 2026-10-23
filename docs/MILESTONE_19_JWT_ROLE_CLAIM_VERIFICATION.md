# Milestone 19 — Signed JWT Role-Claim Boundary Verification

## Objective
Strengthen authorization regression coverage for the optional identity-enabled backend without introducing a live identity provider or learner records.

## Coverage
The signed-JWT HTTP tests verify that an ADMIN can access the account endpoint but cannot access learner-only history; a scalar role claim is not accepted as a role list; unrecognized, case-variant and ROLE_-prefixed values grant no authority; numeric entries grant no authority; and a mixed list grants only its explicitly recognized `EDUCATOR` role.

## Security contract
Only exact `LEARNER`, `EDUCATOR`, and `ADMIN` strings inside the `roles` array can become Spring Security authorities. The learner-history endpoint is reserved for `LEARNER`, and additionally requires an authorized learner record binding.

## Verification and limitations
The pull request's GitHub Actions results are the execution evidence. Local test signing keys and synthetic claims do not demonstrate production identity-provider interoperability, privacy compliance, or deployed authentication.
