-- Prototype-only synthetic demonstration data; no learner identities.
CREATE TABLE demo_assessment_attempts (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 assessment_id VARCHAR(80) NOT NULL,
 correct_count INT NOT NULL,
 total_count INT NOT NULL,
 percentage DECIMAL(5,2) NOT NULL,
 recommendation VARCHAR(32) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE demo_attempt_answers (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 attempt_id BIGINT NOT NULL,
 question_id VARCHAR(80) NOT NULL,
 option_id VARCHAR(80) NOT NULL,
 CONSTRAINT fk_demo_answer_attempt FOREIGN KEY (attempt_id) REFERENCES demo_assessment_attempts(id),
 CONSTRAINT uq_demo_attempt_question UNIQUE (attempt_id,question_id)
);
CREATE INDEX idx_demo_attempt_assessment ON demo_assessment_attempts(assessment_id);
