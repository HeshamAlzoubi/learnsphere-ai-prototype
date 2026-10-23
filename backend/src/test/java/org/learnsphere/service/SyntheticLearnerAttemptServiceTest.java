package org.learnsphere.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SyntheticLearnerAttemptServiceTest {
    @Autowired JdbcTemplate jdbc;
    @Autowired SyntheticLearnerAttemptService service;

    private JwtAuthenticationToken token(String issuer, String subject, String role) {
        Jwt jwt = Jwt.withTokenValue("synthetic-token")
                .header("alg", "none")
                .issuer(issuer)
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        return new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    private void seed() {
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-d-a", "Synthetic A");
        jdbc.update("INSERT INTO learner_organizations(id,display_label) VALUES (?,?)", "org-d-b", "Synthetic B");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-d-a", "org-d-a", "Synthetic Learner A");
        jdbc.update("INSERT INTO synthetic_learners(id,organization_id,display_label) VALUES (?,?,?)",
                "learner-d-b", "org-d-b", "Synthetic Learner B");
        jdbc.update("INSERT INTO learner_identity_bindings(organization_id,learner_id,issuer,subject_id) VALUES (?,?,?,?)",
                "org-d-a", "learner-d-a", "https://issuer.example.test", "subject-d-a");
        jdbc.update("INSERT INTO learner_identity_bindings(organization_id,learner_id,issuer,subject_id) VALUES (?,?,?,?)",
                "org-d-b", "learner-d-b", "https://issuer.example.test", "subject-d-b");
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-d-a", "learner-d-a", "assessment-a", 2, 3, 66.67, "PRACTICE");
        jdbc.update("INSERT INTO learner_assessment_attempts(organization_id,learner_id,assessment_id,correct_count,total_count,percentage,recommendation) VALUES (?,?,?,?,?,?,?)",
                "org-d-b", "learner-d-b", "assessment-b", 3, 3, 100.0, "ADVANCE");
    }

    @Test void learnersSeeOnlyTheirOwnAttemptsAcrossOrganizations() {
        seed();
        var a = service.ownAttempts(token("https://issuer.example.test", "subject-d-a", "LEARNER"));
        var b = service.ownAttempts(token("https://issuer.example.test", "subject-d-b", "LEARNER"));
        assertEquals(1, a.size());
        assertEquals("assessment-a", a.get(0).get("assessmentId"));
        assertEquals(1, b.size());
        assertEquals("assessment-b", b.get(0).get("assessmentId"));
    }

    @Test void missingBindingCannotAccessOtherLearners() {
        seed();
        assertThrows(AccessDeniedException.class, () ->
                service.ownAttempts(token("https://issuer.example.test", "unmapped-subject", "LEARNER")));
    }

    @Test void wrongIssuerCannotAccessBoundSubject() {
        seed();
        assertThrows(AccessDeniedException.class, () ->
                service.ownAttempts(token("https://different-issuer.example.test", "subject-d-a", "LEARNER")));
    }

    @Test void educatorRoleDoesNotImplyLearnerOwnership() {
        seed();
        assertThrows(AccessDeniedException.class, () ->
                service.ownAttempts(token("https://issuer.example.test", "subject-d-a", "EDUCATOR")));
    }
}
