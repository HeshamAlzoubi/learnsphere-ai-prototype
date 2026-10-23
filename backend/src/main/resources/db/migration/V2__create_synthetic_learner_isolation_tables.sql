-- Milestone 13 Gate C: synthetic identities only; no personal data and no learner APIs.
-- Every learner-scoped reference includes organization_id to prevent cross-tenant links.
CREATE TABLE learner_organizations (
 id VARCHAR(64) NOT NULL PRIMARY KEY,
 display_label VARCHAR(120) NOT NULL
);
CREATE TABLE synthetic_learners (
 id VARCHAR(64) NOT NULL,
 organization_id VARCHAR(64) NOT NULL,
 display_label VARCHAR(120) NOT NULL,
 PRIMARY KEY (organization_id, id),
 CONSTRAINT fk_learner_org FOREIGN KEY (organization_id)
  REFERENCES learner_organizations(id)
);
CREATE TABLE learner_classrooms (
 id VARCHAR(64) NOT NULL,
 organization_id VARCHAR(64) NOT NULL,
 display_label VARCHAR(120) NOT NULL,
 PRIMARY KEY (organization_id, id),
 CONSTRAINT fk_classroom_org FOREIGN KEY (organization_id)
  REFERENCES learner_organizations(id)
);
CREATE TABLE learner_enrollments (
 organization_id VARCHAR(64) NOT NULL,
 classroom_id VARCHAR(64) NOT NULL,
 learner_id VARCHAR(64) NOT NULL,
 PRIMARY KEY (organization_id, classroom_id, learner_id),
 CONSTRAINT fk_enrollment_classroom FOREIGN KEY (organization_id, classroom_id)
  REFERENCES learner_classrooms(organization_id, id),
 CONSTRAINT fk_enrollment_learner FOREIGN KEY (organization_id, learner_id)
  REFERENCES synthetic_learners(organization_id, id)
);
CREATE TABLE learner_assessment_attempts (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 organization_id VARCHAR(64) NOT NULL,
 learner_id VARCHAR(64) NOT NULL,
 assessment_id VARCHAR(80) NOT NULL,
 correct_count INT NOT NULL,
 total_count INT NOT NULL,
 percentage DECIMAL(5,2) NOT NULL,
 recommendation VARCHAR(32) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_learner_attempt_owner FOREIGN KEY (organization_id, learner_id)
  REFERENCES synthetic_learners(organization_id, id),
 CONSTRAINT ck_learner_attempt_counts CHECK (correct_count >= 0 AND total_count > 0 AND correct_count <= total_count),
 CONSTRAINT ck_learner_attempt_percentage CHECK (percentage >= 0 AND percentage <= 100)
);
CREATE INDEX idx_learner_attempt_scope ON learner_assessment_attempts(organization_id, learner_id, id);
