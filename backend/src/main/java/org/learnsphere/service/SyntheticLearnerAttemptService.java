package org.learnsphere.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Read-only, synthetic learner attempt history. Ownership comes exclusively
 * from the validated JWT issuer + subject binding, never a client-supplied ID.
 * No HTTP learner-data endpoint is enabled in Gate D.
 */
@Service
public class SyntheticLearnerAttemptService {
    private final JdbcTemplate jdbc;

    public SyntheticLearnerAttemptService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> ownAttempts(JwtAuthenticationToken token) {
        if (token == null || token.getToken() == null
                || !token.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LEARNER"))) {
            throw new AccessDeniedException("Learner role required");
        }
        String issuer = token.getToken().getIssuer() == null ? null
                : token.getToken().getIssuer().toString();
        String subject = token.getToken().getSubject();
        if (issuer == null || issuer.isBlank() || subject == null || subject.isBlank()) {
            throw new AccessDeniedException("Issuer and subject required");
        }
        List<Map<String, Object>> owners = jdbc.queryForList(
                "SELECT organization_id, learner_id FROM learner_identity_bindings WHERE issuer=? AND subject_id=?",
                issuer, subject);
        if (owners.size() != 1) {
            throw new AccessDeniedException("No unique learner binding");
        }
        String organizationId = (String) owners.get(0).get("organization_id");
        String learnerId = (String) owners.get(0).get("learner_id");
        return jdbc.query(
                "SELECT assessment_id, correct_count, total_count, percentage, recommendation, created_at "
                    + "FROM learner_assessment_attempts WHERE organization_id=? AND learner_id=? ORDER BY id DESC",
                (rs, rowNum) -> Map.<String, Object>of(
                    "assessmentId", rs.getString("assessment_id"),
                    "correct", rs.getInt("correct_count"),
                    "total", rs.getInt("total_count"),
                    "percentage", rs.getBigDecimal("percentage"),
                    "recommendation", rs.getString("recommendation"),
                    "createdAt", rs.getTimestamp("created_at").toInstant().toString()),
                organizationId, learnerId);
    }
}
