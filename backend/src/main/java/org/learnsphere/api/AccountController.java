package org.learnsphere.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/** Identity-only endpoint: no roster, learner profile or progress records. */
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwt)) {
            throw new org.springframework.security.access.AccessDeniedException("JWT required");
        }
        List<String> roles = jwt.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring(5))
            .sorted().toList();
        return Map.of("subject", jwt.getToken().getSubject(), "roles", roles);
    }
}
