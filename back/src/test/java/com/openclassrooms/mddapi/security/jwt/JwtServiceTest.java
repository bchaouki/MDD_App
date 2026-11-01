package com.openclassrooms.mddapi.security.jwt;

import com.openclassrooms.mddapi.config.JwtConfig;
import com.openclassrooms.mddapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-for-unit-tests-only-0123456789abcdef";

    @Test
    void generatedTokenCarriesUserIdAndUsername() {
        JwtProperties properties = new JwtProperties(SECRET, 24);
        JwtConfig config = new JwtConfig(properties);
        JwtService service = new JwtService(properties, config.jwtEncoder());

        String token = service.generateToken(authenticationFor(42L, "alice"));
        Jwt jwt = config.jwtDecoder().decode(token);

        assertThat(jwt.getSubject()).isEqualTo("42");
        assertThat(jwt.getClaimAsString("username")).isEqualTo("alice");
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofHours(24));
    }

    @Test
    void negativeExpirationIsRefused() {
        JwtProperties properties = new JwtProperties(SECRET, -1);
        JwtService service = new JwtService(properties, new JwtConfig(properties).jwtEncoder());

        assertThatThrownBy(() -> service.generateToken(authenticationFor(1L, "alice")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        JwtProperties otherProperties = new JwtProperties(SECRET.replace('t', 'x'), 24);
        JwtService other = new JwtService(otherProperties, new JwtConfig(otherProperties).jwtEncoder());
        JwtDecoder decoder = new JwtConfig(new JwtProperties(SECRET, 24)).jwtDecoder();

        String token = other.generateToken(authenticationFor(1L, "alice"));

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void garbageTokenIsRejected() {
        JwtDecoder decoder = new JwtConfig(new JwtProperties(SECRET, 24)).jwtDecoder();

        assertThatThrownBy(() -> decoder.decode("not-a-jwt")).isInstanceOf(JwtException.class);
    }

    @Test
    void refusesPrincipalThatIsNotUserPrincipal() {
        JwtProperties properties = new JwtProperties(SECRET, 24);
        JwtService service = new JwtService(properties, new JwtConfig(properties).jwtEncoder());
        Authentication authentication = new UsernamePasswordAuthenticationToken("alice@mail.com", null, List.of());

        assertThatThrownBy(() -> service.generateToken(authentication))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Authentication authenticationFor(Long id, String username) {
        return new UsernamePasswordAuthenticationToken(new UserPrincipal(id, username, ""), null, List.of());
    }
}
