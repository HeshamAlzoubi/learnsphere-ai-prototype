package org.learnsphere.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SignedJwtSecurityFilterTest.LocalKeys.class)
@TestPropertySource(properties = "learnsphere.identity.enabled=true")
class SignedJwtSecurityFilterTest {
    private static final String ISSUER = "https://synthetic-issuer.example.test";
    private static final String AUDIENCE = "learnsphere-synthetic-api";
    private static final RSAKey TRUSTED_KEY = generateKey();
    private static final RSAKey UNTRUSTED_KEY = generateKey();

    @Autowired MockMvc mvc;

    private static RSAKey generateKey() {
        try {
            return new RSAKeyGenerator(2048).keyID("synthetic-test-key").generate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @TestConfiguration
    static class LocalKeys {
        @Bean
        @Primary
        JwtDecoder signedTestDecoder() throws Exception {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(TRUSTED_KEY.toRSAPublicKey()).build();
            decoder.setJwtValidator(DemoAccessPolicy.issuerAudienceValidator(ISSUER, AUDIENCE));
            return decoder;
        }
    }

    private String signedToken(RSAKey key, String issuer, String audience, Instant expiresAt, Object roles)
            throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject("synthetic-signed-learner")
                .audience(audience)
                .issueTime(Date.from(Instant.now().minusSeconds(30)))
                .expirationTime(Date.from(expiresAt))
                .claim("roles", roles)
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(key.toPrivateKey()));
        return jwt.serialize();
    }

    private String validToken() throws Exception {
        return signedToken(TRUSTED_KEY, ISSUER, AUDIENCE, Instant.now().plusSeconds(600), List.of("LEARNER"));
    }

    @Test void signedValidTokenPassesCompleteHttpSecurityChain() throws Exception {
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + validToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subject").value("synthetic-signed-learner"));
    }

    @Test void accountIdentityResponseIsNotCacheable() throws Exception {
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + validToken()))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
        mvc.perform(get("/api/v1/account/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }


    private String signedTokenWithSubject(String subject) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject(subject)
                .audience(AUDIENCE)
                .issueTime(Date.from(Instant.now().minusSeconds(30)))
                .expirationTime(Date.from(Instant.now().plusSeconds(600)))
                .claim("roles", List.of("LEARNER"))
                .build();
        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(TRUSTED_KEY.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(TRUSTED_KEY.toPrivateKey()));
        return jwt.serialize();
    }

    @Test void missingSubjectIsRejectedEvenWithValidSignatureAndRole() throws Exception {
        mvc.perform(get("/api/v1/account/me")
                .header("Authorization", "Bearer " + signedTokenWithSubject(null)))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    @Test void blankSubjectIsRejectedEvenWithValidSignatureAndRole() throws Exception {
        mvc.perform(get("/api/v1/account/me")
                .header("Authorization", "Bearer " + signedTokenWithSubject("   ")))
            .andExpect(status().isUnauthorized());
    }

    @Test void wrongAudienceRejected() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, "different-api",
                Instant.now().plusSeconds(600), List.of("LEARNER"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test void wrongIssuerRejected() throws Exception {
        String token = signedToken(TRUSTED_KEY, "https://other-issuer.example.test", AUDIENCE,
                Instant.now().plusSeconds(600), List.of("LEARNER"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test void expiredTokenRejected() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().minusSeconds(600), List.of("LEARNER"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test void tokenSignedByUntrustedKeyRejected() throws Exception {
        String token = signedToken(UNTRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of("LEARNER"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test void signedTokenWithWrongRoleForbidden() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of("GUEST"));
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test void missingBearerTokenCannotAccessAccountOrLearnerHistory() throws Exception {
        mvc.perform(get("/api/v1/account/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/learner/me/attempts")).andExpect(status().isUnauthorized())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    @Test void educatorCannotReadLearnerHistory() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of("EDUCATOR"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles[0]").value("EDUCATOR"));
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }


    @Test void administratorCannotReadLearnerHistory() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of("ADMIN"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    @Test void roleClaimMustBeAListNotAString() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), "LEARNER");
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test void unrecognizedAndCaseVariantRolesDoNotGrantAccess() throws Exception {
        for (String role : List.of("learner", "ROLE_LEARNER", "GUEST")) {
            String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                    Instant.now().plusSeconds(600), List.of(role));
            mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        }
    }

    @Test void numericRoleClaimDoesNotGrantAccess() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of(42));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test void mixedClaimDoesNotElevateAnUnrecognizedRole() throws Exception {
        String token = signedToken(TRUSTED_KEY, ISSUER, AUDIENCE,
                Instant.now().plusSeconds(600), List.of("GUEST", 42, "EDUCATOR"));
        mvc.perform(get("/api/v1/account/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test void learnerHistoryDenialIsNotCacheable() throws Exception {
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + validToken()))
            .andExpect(status().isForbidden())
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    @Test void validSignedLearnerWithNoDatabaseBindingForbidden() throws Exception {
        mvc.perform(get("/api/v1/learner/me/attempts").header("Authorization", "Bearer " + validToken()))
            .andExpect(status().isForbidden());
    }
}
