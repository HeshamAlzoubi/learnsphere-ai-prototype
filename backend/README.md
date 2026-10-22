# LearnSphere AI — Prototype Increment 03 (proposed MySQL persistence)

Java 21 / Spring Boot 3.3.13 / MySQL / Spring Data JPA / Flyway.

## Scope
- Retains Increment 02 demo lesson catalog and assessment API.
- Adds a transactional write of **synthetic demo assessment attempts and answers** to MySQL.
- Adds two migration-managed demo tables. This is **not** the complete 12-table DOC-007 schema.
- Does not implement authentication, learner identity, production data handling, or educator dashboards.
- Do not expose this unauthenticated prototype publicly.

## Requirements
Java 21, Maven 3.9+ and MySQL 8.0+ (or compatible server). Do not commit credentials.

## Database preparation (MySQL administrator)
Run `mysql/setup_example.sql` after reviewing/editing the placeholder password. Restrict database access to localhost.

## Run (PowerShell in backend folder)
```
$env:LEARN_DB_URL='jdbc:mysql://localhost:3306/learnsphere'
$env:LEARN_DB_USER='learnsphere_app'
$env:LEARN_DB_PASSWORD='your-local-secret'
mvn clean test
mvn spring-boot:run
```
For tests, an in-memory H2 database in MySQL compatibility mode is used. Tests do **not** prove MySQL integration works; test MySQL separately.

## Manual API example
GET http://localhost:8080/api/v1/lessons
POST http://localhost:8080/api/v1/assessments/assessment-001/submissions
Body: `{"answers":[{"questionId":"q1","optionId":"b"},{"questionId":"q2","optionId":"c"},{"questionId":"q3","optionId":"a"}]}`

Expected scoring result is 100% / ADVANCE. After successful POST, inspect `demo_assessment_attempts` and `demo_attempt_answers` in your local MySQL database.

## Verification status
Source and migration reviewed statically. Full Maven dependency resolution, Spring context tests, and MySQL integration were **not executed** in the authoring environment. Run tests before relying on the package.
