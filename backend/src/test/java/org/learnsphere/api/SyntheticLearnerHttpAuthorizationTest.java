package org.learnsphere.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties="learnsphere.identity.enabled=true")
class SyntheticLearnerHttpAuthorizationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @MockBean JwtDecoder jwtDecoder;

    private void seed() {
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-http-a", "Synthetic A");
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-http-b", "Synthetic B");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-http-a", "org-http-a", "Synthetic A");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-http-b", "org-http-b", "Synthetic B");
        jdbc.update("INSERT INTO learner_identity_bindings(organization_id,learner_id,issuer,subject_id) VALUES (?,?,?,?)",
                "org-http-a", "learner-http-a", "https://issuer.example.test", "subject-http-a");
        jdbc.update("INSERT INTO learner_identity_bindings(organization_id,learner_id,issuer,subject_id) VALUES (?,?,?,?)",
                "org-http-b", "learner-http-b", "https://issuer.example.test", "subject-http-b");
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-http-a", "learner-http-a", "assessment-http-a", 2, 3, 66.67, "PRACTICE");
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-http-b", "learner-http-b", "assessment-http-b", 3, 3, 100.0, "ADVANCE");
    }

    @Test void anonymousCannotReadAttempts() throws Exception {
        mvc.perform(get("/api/v1/learner/me/attempts")).andExpect(status().isUnauthorized());
    }

    @Test void educatorCannotReadLearnerAttempts() throws Exception {
        mvc.perform(get("/api/v1/learner/me/attempts").with(jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_EDUCATOR"))))
            .andExpect(status().isForbidden());
    }

    @Test void learnerOnlySeesTheirOwnSyntheticAttempts() throws Exception {
        seed();
        mvc.perform(get("/api/v1/learner/me/attempts").with(jwt().jwt(j ->
                j.issuer("https://issuer.example.test").subject("subject-http-a")
                 .claim("roles", List.of("LEARNER")))
                .authorities(new SimpleGrantedAuthority("ROLE_LEARNER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].assessmentId").value("assessment-http-a"))
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test void unboundSubjectIsDenied() throws Exception {
        seed();
        mvc.perform(get("/api/v1/learner/me/attempts").with(jwt().jwt(j ->
                j.issuer("https://issuer.example.test").subject("unbound-subject"))
                .authorities(new SimpleGrantedAuthority("ROLE_LEARNER"))))
            .andExpect(status().isForbidden());
    }

    @Test void wrongIssuerIsDenied() throws Exception {
        seed();
        mvc.perform(get("/api/v1/learner/me/attempts").with(jwt().jwt(j ->
                j.issuer("https://other-issuer.example.test").subject("subject-http-a"))
                .authorities(new SimpleGrantedAuthority("ROLE_LEARNER"))))
            .andExpect(status().isForbidden());
    }

    @Test void callerSuppliedLearnerIdCannotOpenOtherRecords() throws Exception {
        seed();
        mvc.perform(get("/api/v1/learners/learner-http-b/progress").with(jwt().jwt(j ->
                j.issuer("https://issuer.example.test").subject("subject-http-a"))
                .authorities(new SimpleGrantedAuthority("ROLE_LEARNER"))))
            .andExpect(status().isForbidden());
    }
}
