package org.learnsphere.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Security-boundary regression cases; require a real Maven test run. */
@SpringBootTest
@AutoConfigureMockMvc
class AccessBoundaryIntegrationTest {
    @Autowired MockMvc mvc;

    @Test void lessonAssessmentIsPublicDemo() throws Exception {
        mvc.perform(get("/api/v1/lessons/lesson-001/assessment"))
           .andExpect(status().isOk());
    }
    @Test void demoProgressIsPublicDemo() throws Exception {
        mvc.perform(get("/api/v1/demo-progress"))
           .andExpect(status().isOk());
    }
    @Test void hypotheticalLearnerRecordsAreDenied() throws Exception {
        mvc.perform(get("/api/v1/learners/demo-learner/progress"))
           .andExpect(status().isForbidden());
    }
    @Test void hypotheticalAdminEndpointIsDenied() throws Exception {
        mvc.perform(get("/api/v1/admin/users"))
           .andExpect(status().isForbidden());
    }
    @Test void nestedUnimplementedLessonRouteIsDenied() throws Exception {
        mvc.perform(get("/api/v1/lessons/lesson-001/private/records"))
           .andExpect(status().isForbidden());
    }
    @Test void unsupportedDemoMutationIsDenied() throws Exception {
        mvc.perform(delete("/api/v1/lessons/lesson-001"))
           .andExpect(status().isForbidden());
    }
    @Test void unknownSubmissionMethodIsDenied() throws Exception {
        mvc.perform(put("/api/v1/assessments/assessment-001/submissions")
           .contentType(MediaType.APPLICATION_JSON).content("{}"))
           .andExpect(status().isForbidden());
    }
}
