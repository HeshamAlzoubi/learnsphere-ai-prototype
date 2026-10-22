package org.learnsphere.persistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class DemoPersistenceTest {
 @Autowired DemoAttemptRepository repo;
 @Autowired EntityManager entityManager;
 @Test @Transactional void persistsAttemptAndAnswers(){
  var attempt=new DemoAttemptEntity("assessment-001",2,3,66.67,"PRACTICE");
  attempt.addAnswer("q1","a");attempt.addAnswer("q2","c");attempt.addAnswer("q3","b");
  var saved=repo.saveAndFlush(attempt);
  assertNotNull(saved.getId());
  Long savedId=saved.getId();
  entityManager.flush();
  entityManager.clear();
  assertEquals(3,repo.findById(savedId).orElseThrow().getAnswers().size());
 }
}
