package org.learnsphere.api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class BackendHttpIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void lessonsListed() throws Exception {mvc.perform(get("/api/v1/lessons")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("lesson-001"));}
 @Test void assessmentDoesNotExposeAnswerKey() throws Exception {mvc.perform(get("/api/v1/assessments/assessment-001")).andExpect(status().isOk()).andExpect(jsonPath("$.questions.length()").value(3)).andExpect(jsonPath("$.key").doesNotExist());}
 @Test void submitPerfectAnswers() throws Exception {mvc.perform(post("/api/v1/assessments/assessment-001/submissions").contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[{\"questionId\":\"q1\",\"optionId\":\"b\"},{\"questionId\":\"q2\",\"optionId\":\"c\"},{\"questionId\":\"q3\",\"optionId\":\"a\"}]}")).andExpect(status().isOk()).andExpect(jsonPath("$.percentage").value(100.0)).andExpect(jsonPath("$.recommendation").value("ADVANCE"));}
 @Test void duplicateAnswerRejected() throws Exception {mvc.perform(post("/api/v1/assessments/assessment-001/submissions").contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[{\"questionId\":\"q1\",\"optionId\":\"b\"},{\"questionId\":\"q1\",\"optionId\":\"a\"},{\"questionId\":\"q3\",\"optionId\":\"a\"}]}")).andExpect(status().isBadRequest());}
 @Test void expandedCatalogAndOptionLabels() throws Exception {mvc.perform(get("/api/v1/lessons")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));mvc.perform(get("/api/v1/lessons/lesson-002/assessment")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value("assessment-002")).andExpect(jsonPath("$.questions[0].options[0].text").isNotEmpty());}
 @Test void missingLesson() throws Exception {mvc.perform(get("/api/v1/lessons/missing")).andExpect(status().isNotFound());}
}
