package org.learnsphere.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Future identity routes must remain closed until authenticated authorization is implemented. */
@SpringBootTest
@AutoConfigureMockMvc
class LearnerIdentityBoundaryTest {
    @Autowired MockMvc mvc;

    @Test void unauthenticatedLearnerProgressIsDenied() throws Exception {
        mvc.perform(get("/api/v1/learners/learner-001/progress"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedLearnerCreationIsDenied() throws Exception {
        mvc.perform(post("/api/v1/learners")
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedEducatorRosterIsDenied() throws Exception {
        mvc.perform(get("/api/v1/educators/educator-001/learners"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedOrganizationRosterIsDenied() throws Exception {
        mvc.perform(get("/api/v1/organizations/org-001/learners"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedLearnerAttemptCreationIsDenied() throws Exception {
        mvc.perform(post("/api/v1/learners/learner-001/attempts")
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedLearnerDeletionIsDenied() throws Exception {
        mvc.perform(delete("/api/v1/learners/learner-001"))
            .andExpect(status().isForbidden());
    }

    @Test void unauthenticatedAccountRegistrationIsDenied() throws Exception {
        mvc.perform(post("/api/v1/accounts/register")
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test void syntheticDemoCatalogRemainsAccessible() throws Exception {
        mvc.perform(get("/api/v1/lessons")).andExpect(status().isOk());
    }
}
