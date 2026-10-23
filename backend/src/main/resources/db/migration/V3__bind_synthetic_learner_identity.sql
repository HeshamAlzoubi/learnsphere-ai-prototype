-- Gate D: issuer-subject binding is unique within an organization.
-- No public account provisioning API is added.
CREATE TABLE learner_identity_bindings (
 organization_id VARCHAR(64) NOT NULL,
 learner_id VARCHAR(64) NOT NULL,
 issuer VARCHAR(255) NOT NULL,
 subject_id VARCHAR(255) NOT NULL,
 PRIMARY KEY (organization_id, learner_id),
 CONSTRAINT uq_identity_issuer_subject UNIQUE (issuer, subject_id),
 CONSTRAINT fk_identity_learner FOREIGN KEY (organization_id, learner_id)
  REFERENCES synthetic_learners(organization_id, id)
);
CREATE INDEX idx_identity_scope ON learner_identity_bindings(issuer, subject_id, organization_id);
