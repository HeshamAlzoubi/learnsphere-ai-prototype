package org.learnsphere.api;

import org.learnsphere.service.SyntheticLearnerAttemptService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/** Authenticated, read-only synthetic learner self-service route. */
@RestController
@RequestMapping("/api/v1/learner/me")
public class SyntheticLearnerAttemptController {
    private final SyntheticLearnerAttemptService service;

    public SyntheticLearnerAttemptController(SyntheticLearnerAttemptService service) {
        this.service = service;
    }

    @GetMapping("/attempts")
    public List<Map<String, Object>> ownAttempts(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwt)) {
            throw new AccessDeniedException("JWT required");
        }
        return service.ownAttempts(jwt);
    }
}
