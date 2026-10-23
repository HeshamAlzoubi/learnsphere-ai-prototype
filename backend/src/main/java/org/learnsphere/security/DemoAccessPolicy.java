package org.learnsphere.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

/**
 * Local synthetic demo remains public. Optional identity endpoint requires a JWT
 * validated by a configured trusted issuer; disabled by default.
 * No learner data routes or registration endpoints are enabled.
 */
@Configuration
@EnableWebSecurity
public class DemoAccessPolicy {
    @Bean
    SecurityFilterChain demoSecurityFilterChain(
            HttpSecurity http,
            @Value("${learnsphere.identity.enabled:false}") boolean identityEnabled) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .requestCache(cache -> cache.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.GET, "/api/v1/lessons", "/api/v1/lessons/*",
                    "/api/v1/lessons/*/assessment", "/api/v1/assessments/*",
                    "/api/v1/demo-progress").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/api/v1/assessments/*/submissions").permitAll();
                if (identityEnabled) {
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/account/me")
                        .hasAnyRole("LEARNER", "EDUCATOR", "ADMIN");
                }
                auth.anyRequest().denyAll();
            });
        if (identityEnabled) {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                Object claim = jwt.getClaim("roles");
                if (!(claim instanceof List<?> roles)) return List.of();
                return roles.stream()
                    .filter(String.class::isInstance).map(String.class::cast)
                    .filter(role -> role.equals("LEARNER") || role.equals("EDUCATOR") || role.equals("ADMIN"))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
            });
            http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        }
        return http.build();
    }
}
