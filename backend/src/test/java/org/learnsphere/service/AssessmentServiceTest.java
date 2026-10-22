package org.learnsphere.service;
import org.junit.jupiter.api.Test;
import org.learnsphere.api.*;
import org.learnsphere.catalog.DemoCatalog;
import org.learnsphere.persistence.DemoAttemptRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AssessmentServiceTest {
 private final DemoAttemptRepository repo=mock(DemoAttemptRepository.class);
 private final AssessmentService service=new AssessmentService(new DemoCatalog(),repo);
 @Test void correctSubmission(){var response=service.submit("assessment-001",new SubmissionRequest(List.of(new SubmissionRequest.Answer("q2","c"),new SubmissionRequest.Answer("q1","b"),new SubmissionRequest.Answer("q3","a"))));assertEquals(100.0,response.percentage());verify(repo).saveAndFlush(any());}
 @Test void duplicateRejected(){assertThrows(IllegalArgumentException.class,()->service.submit("assessment-001",new SubmissionRequest(List.of(new SubmissionRequest.Answer("q1","a"),new SubmissionRequest.Answer("q1","b"),new SubmissionRequest.Answer("q3","a")))));verifyNoInteractions(repo);}
 @Test void invalidOptionRejectedWithoutSaving(){assertThrows(IllegalArgumentException.class,()->service.submit("assessment-001",new SubmissionRequest(List.of(new SubmissionRequest.Answer("q1","z"),new SubmissionRequest.Answer("q2","c"),new SubmissionRequest.Answer("q3","a")))));verifyNoInteractions(repo);}
 @Test void missingAnswerRejectedWithoutSaving(){assertThrows(IllegalArgumentException.class,()->service.submit("assessment-001",new SubmissionRequest(List.of(new SubmissionRequest.Answer("q1","b")))));verifyNoInteractions(repo);}
 @Test void secondAssessmentScoresIndependently(){var response=service.submit("assessment-002",new SubmissionRequest(List.of(new SubmissionRequest.Answer("c1","a"),new SubmissionRequest.Answer("c2","b"),new SubmissionRequest.Answer("c3","c"))));assertEquals(100.0,response.percentage());assertEquals("ADVANCE",response.recommendation());}
 @Test void thirdAssessmentScoresIndependently(){var response=service.submit("assessment-003",new SubmissionRequest(List.of(new SubmissionRequest.Answer("l1","b"),new SubmissionRequest.Answer("l2","a"),new SubmissionRequest.Answer("l3","c"))));assertEquals(100.0,response.percentage());}
 @Test void crossAssessmentQuestionRejected(){assertThrows(IllegalArgumentException.class,()->service.submit("assessment-002",new SubmissionRequest(List.of(new SubmissionRequest.Answer("q1","b"),new SubmissionRequest.Answer("c2","b"),new SubmissionRequest.Answer("c3","c")))));verifyNoInteractions(repo);}
 @Test void unknownRejected(){assertThrows(java.util.NoSuchElementException.class,()->service.submit("missing",new SubmissionRequest(List.of())));verifyNoInteractions(repo);}
}
