package org.learnsphere.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Full Spring security regression tests; executed by the backend CI workflow. */
@SpringBootTest @AutoConfigureMockMvc
class DemoAccessPolicyTest {
    @Autowired MockMvc mvc;
    @Test void lessonCatalogStillAccessible() throws Exception {
        mvc.perform(get("/api/v1/lessons")).andExpect(status().isOk());
    }
    @Test void syntheticSubmissionStillReachesValidation() throws Exception {
        mvc.perform(post("/api/v1/assessments/assessment-001/submissions")
                .contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[]}"))
                .andExpect(status().isBadRequest());
    }
    @Test void learnerEndpointIsForbidden() throws Exception {
        mvc.perform(get("/api/v1/learners/self/progress")).andExpect(status().isForbidden());
    }
    @Test void educatorEndpointIsForbidden() throws Exception {
        mvc.perform(get("/api/v1/educators/classes")).andExpect(status().isForbidden());
    }
    @Test void administratorEndpointIsForbidden() throws Exception {
        mvc.perform(get("/api/v1/admin/users")).andExpect(status().isForbidden());
    }
    @Test void protectedIdentityEndpointsStayClosedByDefault() throws Exception {
        mvc.perform(get("/api/v1/account/me")).andExpect(status().isForbidden())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
        mvc.perform(get("/api/v1/learner/me/attempts")).andExpect(status().isForbidden())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }
    @Test void suppliedBearerTokenCannotBypassDisabledIdentityMode() throws Exception {
        mvc.perform(get("/api/v1/account/me")
                .header("Authorization", "Bearer synthetic-untrusted-token"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/learner/me/attempts")
                .header("Authorization", "Bearer synthetic-untrusted-token"))
            .andExpect(status().isForbidden());
    }
    @Test void publicDemoDoesNotExposeProtectedIdentityRoutes() throws Exception {
        mvc.perform(get("/api/v1/demo-progress")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/account/me")).andExpect(status().isForbidden());
    }
    @Test void unknownWriteEndpointIsForbidden() throws Exception {
        mvc.perform(post("/api/v1/lessons").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }
}
