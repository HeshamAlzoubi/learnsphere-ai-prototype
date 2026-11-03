# LearnSphere AI — Adaptive STEM Learning Platform

**Early-stage prototype · Founded and developed by Hesham Al Zoubi**

LearnSphere AI is an educational technology project working toward a cloud-based platform that helps students learn STEM concepts through guided lessons, practice, feedback, and learning-progress insights. Its long-term goal is to help educators identify learning needs and support more personalized instruction, including for underserved learners.

The current prototype focuses on **middle school, grades 6–8**, using introductory computing concepts. The broader vision includes K–12 schools, community colleges, and universities.

> **Project status:** This repository contains prototype source code, not a hosted application. It is not ready for real student use. Use synthetic demonstration data only. Current recommendations are rule-based; trained AI models and live sign-in are not integrated.

## Why LearnSphere AI?

Students learn at different speeds and may need different kinds of practice. Educators need practical ways to understand those differences and provide timely support.

LearnSphere AI aims to address these needs through:

| Audience | Intended benefit |
| --- | --- |
| Students | Clear explanations, targeted practice, helpful feedback, and engaging activities that encourage critical thinking and problem-solving. |
| Teachers | Progress insights to help identify learning gaps and inform instructional decisions. |
| Schools and institutions | A foundation for exploring scalable STEM support and evaluating learning needs across educational settings. |

These are project goals, not measured educational outcomes. Classroom effectiveness and institutional impact still require evaluation.

## Current prototype

The repository includes:

- A lesson catalog and demonstration content covering algorithms, conditions, and loops.
- Assessment submission and backend scoring.
- Rule-based recommendations to reinforce concepts, practice further, or advance.
- Shared synthetic demo progress and progress-trend logic.
- MySQL persistence and Flyway database migrations for demonstration and synthetic learner records.
- A learner-history interface with session-scoped requests and stale-request cancellation.
- JWT authorization boundaries and regression tests using synthetic identities and signed test tokens.
- Automated backend, frontend, browser, and MySQL integration workflow definitions.

**Shared demo progress is not private learner history.** The protected learner-history interface is signed out by default, and a trusted live identity provider remains a future dependency.

## Planned AI capabilities

Future work will explore AI to:

- Personalize learning paths and recommend practice based on learning needs.
- Provide contextual explanations and feedback.
- Help educators interpret progress patterns and identify areas needing support.
- Support the development of engaging, age-appropriate STEM activities.

These capabilities are planned and are not claims about the current implementation. AI-assisted features will require educational review, evaluation for accuracy and bias, privacy safeguards, and appropriate human oversight.

## Development roadmap

Expansion is planned in stages, subject to development, feedback, and validation:

| Stage | Focus |
| --- | --- |
| Current prototype | Grades 6–8: demonstrate the lesson, assessment, feedback, and progress flow using synthetic data. |
| Planned expansion 1 | Elementary school, K–5: develop age-appropriate content and interactions. |
| Planned expansion 2 | High school, grades 9–12: broaden STEM content and learning activities. |
| Planned expansion 3 | Community colleges and universities: extend content and workflows for higher education. |

Supporting work includes trusted sign-in, learner provisioning, educator tools, richer progress analytics, future AI integration, accessibility review, secure hosting, and pilot preparation. The roadmap is a direction for development, not a committed delivery schedule.

## Technology

| Layer | Current technology |
| --- | --- |
| Backend | Java 21, Spring Boot, Spring Data JPA |
| Frontend | React, TypeScript, Vite |
| Database | MySQL, Flyway migrations |
| Verification | Backend and frontend tests, browser E2E, GitHub Actions |

## Explore and run locally

Start with the [backend setup notes](backend/README.md) and [frontend setup notes](frontend/README.md). Those files include earlier increment-specific notes; consult the current source, workflows, and verification documents for later changes.

Requirements include JDK 21, Maven, Node.js 20+ with npm, and a configured local MySQL instance for backend runtime.

### Backend verification

From the repository root:

```bash
cd backend
mvn -B clean verify
```

Configure the local database as described in the backend notes before starting the application with `mvn spring-boot:run`. Keep credentials out of source control.

### Frontend verification and development

In a separate terminal, from the repository root:

```bash
cd frontend
npm install
npm run typecheck
npm test
npm run build
npm run dev
```

Use `npm ci` instead of `npm install` when a committed, matching lockfile is available.

The backend binds to `127.0.0.1:8080` by default. The Vite frontend uses `127.0.0.1:5173` and proxies API requests to the backend.

A separate core-only check is available:

```bash
python tools/verify_core.py
```

This core check does not verify Spring Boot, MySQL, or React. See the [Actions page](https://github.com/HeshamAlzoubi/learnsphere-ai-prototype/actions) for workflow runs and their actual results.

## Security and readiness

The prototype includes protections for private learner and account responses, such as server-side no-store cache headers, cancellation of stale history requests, and JWT claim validation.

- Protected history requests use `Authorization: Bearer`, cancellation signals, and `cache: 'no-store'`.
- Session changes cancel stale requests and prevent old responses from being displayed.
- Server-side cache protection covers private learner and account identity responses.
- Signed-token regression coverage checks role boundaries and nonblank subject claims.

These controls do not establish production readiness, live authentication, or FERPA compliance. A trusted identity provider, configured JWT issuer/audience and roles, authenticated sessions, secure hosting, monitoring, accessibility review, and student-data governance require implementation and verification before real learner rollout.

Passing automated checks does not replace security review or deployment testing.

## Technical documentation and verification

Detailed development records are available separately:

- [Verification matrix](VERIFICATION_MATRIX.md)
- [GitHub setup and verification](GITHUB_SETUP_AND_VERIFICATION.md)
- [Security architecture](SECURITY_ARCHITECTURE.md)
- [Identity and role-based access design](IDENTITY_RBAC_DESIGN.md)
- [Learner dashboard foundation — Milestone 14](docs/milestone-14-gate-a-learner-dashboard-foundation.md)
- Milestone 15: cancellation error isolation, frontend no-store requests, server-side no-store responses, and documentation closeout; see merged pull requests and CI records.
- [Identity readiness — Milestone 16](docs/MILESTONE_16_IDENTITY_READINESS.md)
- [Private account cache protection — Milestone 17](docs/MILESTONE_17_ACCOUNT_CACHE_PROTECTION.md)
- [Default-deny verification — Milestone 18](docs/MILESTONE_18_DEFAULT_DENY_VERIFICATION.md)
- [JWT role-claim verification — Milestone 19](docs/MILESTONE_19_JWT_ROLE_CLAIM_VERIFICATION.md)
- [JWT subject validation — Milestone 20](docs/MILESTONE_20_JWT_SUBJECT_VALIDATION.md)

## Founder and feedback

**Hesham Al Zoubi**  
Founder, LearnSphere AI  
Assistant Professor, Computing Sciences & Information Technology, Johnson County Community College

LearnSphere AI is presented here as the founder's project; institutional affiliation does not imply college endorsement or partnership.

Feedback from educators, developers, and potential institutional reviewers is welcome. Please [open an issue](https://github.com/HeshamAlzoubi/learnsphere-ai-prototype/issues) with suggestions about educational value, usability, technical design, or interest in a future demonstration. Do not include student records, credentials, or other private information.
