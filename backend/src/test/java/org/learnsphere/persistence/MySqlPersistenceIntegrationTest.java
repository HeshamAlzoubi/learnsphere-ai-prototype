package org.learnsphere.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.learnsphere.api.SubmissionRequest;
import org.learnsphere.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Executes only against the disposable MySQL database provisioned by CI. */
@SpringBootTest
@ActiveProfiles("mysql-it")
@EnabledIfEnvironmentVariable(named="LEARN_MYSQL_IT", matches="true")
class MySqlPersistenceIntegrationTest {
  @Autowired DemoAttemptRepository attempts;
  @Autowired AssessmentService assessments;
  @Autowired JdbcTemplate jdbc;

  @Test void flywayCreatedTablesAndValidatedSchema() {
    Integer versionCount = jdbc.queryForObject(
      "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1", Integer.class);
    assertNotNull(versionCount);
    assertTrue(versionCount >= 1);
    Integer tables = jdbc.queryForObject(
      "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name IN ('demo_assessment_attempts','demo_attempt_answers')", Integer.class);
    assertEquals(2, tables);
  }

  @Test void assessmentSubmissionCommitsAttemptAndThreeAnswers() {
    long before = attempts.count();
    var request = new SubmissionRequest(List.of(
      new SubmissionRequest.Answer("q1","b"),
      new SubmissionRequest.Answer("q2","c"),
      new SubmissionRequest.Answer("q3","a")
    ));
    var response = assessments.submit("assessment-001", request);
    assertEquals("assessment-001", response.assessmentId());
    assertEquals(before + 1, attempts.count());
    var persisted = attempts.findAllByOrderByIdDesc().get(0);
    assertEquals("assessment-001", persisted.getAssessmentId());
    Integer answerCount = jdbc.queryForObject(
      "SELECT COUNT(*) FROM demo_attempt_answers WHERE attempt_id = ?", Integer.class, persisted.getId());
    assertEquals(3, answerCount);
  }
}
