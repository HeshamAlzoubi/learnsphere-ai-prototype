package org.learnsphere.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Source-level regression scenarios; execution requires Maven and dependencies. */
@SpringBootTest @AutoConfigureMockMvc
class BackendSecurityRegressionTest {
 @Autowired MockMvc mvc;
 @Test void baselineHeadersOnCatalog() throws Exception {
  mvc.perform(get("/api/v1/lessons"))
   .andExpect(status().isOk())
   .andExpect(header().string("X-Content-Type-Options", "nosniff"))
   .andExpect(header().string("X-Frame-Options", "DENY"))
   .andExpect(header().string("Referrer-Policy", "no-referrer"))
   .andExpect(header().string("Cache-Control", "no-store"));
 }
 @Test void missingAnswerRejected() throws Exception {
  mvc.perform(post("/api/v1/assessments/assessment-001/submissions")
   .contentType(MediaType.APPLICATION_JSON)
   .content("{\"answers\":[{\"questionId\":\"q1\",\"optionId\":\"b\"}]}"))
   .andExpect(status().isBadRequest());
 }
 @Test void unknownOptionRejected() throws Exception {
  mvc.perform(post("/api/v1/assessments/assessment-001/submissions")
   .contentType(MediaType.APPLICATION_JSON)
   .content("{\"answers\":[{\"questionId\":\"q1\",\"optionId\":\"x\"},{\"questionId\":\"q2\",\"optionId\":\"c\"},{\"questionId\":\"q3\",\"optionId\":\"a\"}]}"))
   .andExpect(status().isBadRequest());
 }
 @Test void noCorrectAnswerKeyExposed() throws Exception {
  mvc.perform(get("/api/v1/assessments/assessment-001"))
   .andExpect(status().isOk())
   .andExpect(jsonPath("$.key").doesNotExist())
   .andExpect(jsonPath("$.correctAnswers").doesNotExist());
 }
}
