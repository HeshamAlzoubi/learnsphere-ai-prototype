package org.learnsphere.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

/**
 * Local synthetic demo remains public. Optional identity endpoint requires a JWT
 * validated by a configured trusted issuer; disabled by default.
 * Only read-only synthetic self-service is enabled; no registration endpoints.
 */
@Configuration
@EnableWebSecurity
public class DemoAccessPolicy {
    @Bean
    @Lazy
    JwtDecoder trustedJwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuer,
            @Value("${learnsphere.identity.audience:}") String audience) {
        if (!StringUtils.hasText(issuer) || !StringUtils.hasText(audience)) {
            throw new IllegalStateException("Identity requires a trusted issuer URI and a nonempty expected audience");
        }
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
        decoder.setJwtValidator(issuerAudienceValidator(issuer, audience));
        return decoder;
    }

    /** Shared by the production decoder and local signed-token security tests. */
    public static OAuth2TokenValidator<Jwt> issuerAudienceValidator(String issuer, String audience) {
        if (!StringUtils.hasText(issuer) || !StringUtils.hasText(audience)) {
            throw new IllegalArgumentException("Issuer and audience are required");
        }
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(audience)
            ? OAuth2TokenValidatorResult.success()
            : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
        return new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), audienceValidator);
    }

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
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/learner/me/attempts")
                        .hasRole("LEARNER");
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
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
            });
            http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        }
        return http.build();
    }
}
