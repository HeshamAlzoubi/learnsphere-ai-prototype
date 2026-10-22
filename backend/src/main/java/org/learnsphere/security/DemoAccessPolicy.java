package org.learnsphere.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Synthetic-data-only access boundary. This is NOT user authentication.
 * Public demo endpoints are accessible only in the local-only deployment configuration.
 * Every other URL is denied; identity, educator and admin APIs are NOT implemented.
 */
@Configuration
@EnableWebSecurity
public class DemoAccessPolicy {
    @Bean
    SecurityFilterChain demoSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Stateless JSON demo submissions do not use browser cookie sessions.
            // Revisit CSRF protection when any cookie-based authentication is introduced.
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .requestCache(cache -> cache.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/v1/lessons", "/api/v1/lessons/*", "/api/v1/lessons/*/assessment", "/api/v1/assessments/*", "/api/v1/demo-progress").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/assessments/*/submissions").permitAll()
                .anyRequest().denyAll())
            .build();
    }
}
