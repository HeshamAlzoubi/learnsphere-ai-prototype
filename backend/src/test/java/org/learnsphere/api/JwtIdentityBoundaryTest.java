package org.learnsphere.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties="learnsphere.identity.enabled=true")
class JwtIdentityBoundaryTest {
    @Autowired MockMvc mvc;
    @MockBean JwtDecoder jwtDecoder;

    @Test void anonymousCannotReadIdentity() throws Exception {
        mvc.perform(get("/api/v1/account/me")).andExpect(status().isUnauthorized());
    }

    @Test void learnerCanReadOnlyOwnTokenIdentity() throws Exception {
        mvc.perform(get("/api/v1/account/me").with(jwt().jwt(j -> j.subject("synthetic-learner-001")
                .claim("roles", List.of("LEARNER")))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subject").value("synthetic-learner-001"))
            .andExpect(jsonPath("$.roles[0]").value("LEARNER"));
    }

    @Test void unrecognizedRoleIsRejected() throws Exception {
        mvc.perform(get("/api/v1/account/me").with(jwt().jwt(j -> j.subject("synthetic-guest")
                .claim("roles", List.of("GUEST")))))
            .andExpect(status().isForbidden());
    }

    @Test void validEducatorStillCannotReadUnimplementedLearnerProgress() throws Exception {
        mvc.perform(get("/api/v1/learners/learner-002/progress")
                .with(jwt().jwt(j -> j.subject("synthetic-educator-001")
                    .claim("roles", List.of("EDUCATOR")))))
            .andExpect(status().isForbidden());
    }

    @Test void tokenWithoutRoleCannotReadIdentity() throws Exception {
        mvc.perform(get("/api/v1/account/me").with(jwt().jwt(j -> j.subject("synthetic-user"))))
            .andExpect(status().isForbidden());
    }
}
