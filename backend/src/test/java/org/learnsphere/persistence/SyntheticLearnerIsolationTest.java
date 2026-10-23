package org.learnsphere.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/** Gate C tests synthetic storage isolation without enabling learner endpoints. */
@SpringBootTest
@Transactional
class SyntheticLearnerIsolationTest {
    @Autowired JdbcTemplate jdbc;

    private void seed() {
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-gc-a", "Synthetic A");
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-gc-b", "Synthetic B");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-gc-a", "org-gc-a", "Synthetic Learner A");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-gc-b", "org-gc-b", "Synthetic Learner B");
        jdbc.update("INSERT INTO learner_classrooms(id,organization_id,display_label) VALUES (?,?,?)",
                "class-gc-a", "org-gc-a", "Synthetic Classroom A");
    }

    @Test void scopedAttemptQueryReturnsOnlyItsOrganizationAndLearner() {
        seed();
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-gc-a", "learner-gc-a", "assessment-001", 2, 3, 66.67, "PRACTICE");
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-gc-b", "learner-gc-b", "assessment-001", 3, 3, 100.0, "ADVANCE");
        Integer own = jdbc.queryForObject("SELECT COUNT(*) FROM learner_assessment_attempts WHERE organization_id=? AND learner_id=?",
                Integer.class, "org-gc-a", "learner-gc-a");
        Integer other = jdbc.queryForObject("SELECT COUNT(*) FROM learner_assessment_attempts WHERE organization_id=? AND learner_id=?",
                Integer.class, "org-gc-a", "learner-gc-b");
        assertEquals(1, own);
        assertEquals(0, other);
    }

    @Test void databaseRejectsCrossOrganizationEnrollment() {
        seed();
        assertThrows(DataIntegrityViolationException.class, () ->
                jdbc.update("INSERT INTO learner_enrollments(organization_id,classroom_id,learner_id) VALUES (?,?,?)",
                        "org-gc-a", "class-gc-a", "learner-gc-b"));
    }

    @Test void databaseRejectsCrossOrganizationAttemptOwnership() {
        seed();
        assertThrows(DataIntegrityViolationException.class, () ->
                jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                        "org-gc-a", "learner-gc-b", "assessment-001", 2, 3, 66.67, "PRACTICE"));
    }

    @Test void databaseRejectsInvalidScore() {
        seed();
        assertThrows(DataIntegrityViolationException.class, () ->
                jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                        "org-gc-a", "learner-gc-a", "assessment-001", 4, 3, 133.33, "ADVANCE"));
    }
}
