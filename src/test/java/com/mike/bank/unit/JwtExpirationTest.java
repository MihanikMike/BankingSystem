package com.mike.bank.unit;

import com.mike.bank.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtExpirationTest {

    @Test
    void expiredJwtShouldBeRejected() throws Exception {
        SecurityConfig config = new SecurityConfig();
        var keyPair = config.jwtKeyPair();
        var encoder = config.jwtEncoder(keyPair);
        var decoder = config.jwtDecoder(keyPair);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("mike")
                .issuedAt(now.minusSeconds(600))
                .expiresAt(now.minusSeconds(300))
                .build();

        String expiredToken = encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        JwtValidationException exception = assertThrows(
                JwtValidationException.class,
                () -> decoder.decode(expiredToken)
        );

        assertTrue(
                exception.getErrors().stream().anyMatch(error ->
                        error.getDescription() != null
                                && error.getDescription().contains("Jwt expired")
                ),
                "Token must be rejected because it has expired"
        );
    }

}
