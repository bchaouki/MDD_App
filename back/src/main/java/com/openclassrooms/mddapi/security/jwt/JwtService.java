package com.openclassrooms.mddapi.security.jwt;



import com.openclassrooms.mddapi.security.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {


    private final long jwtExpiration;


    private final JwtEncoder jwtEncoder;

    public JwtService(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {

        this.jwtExpiration = jwtProperties.jwtExpiration();
        this.jwtEncoder=jwtEncoder;
    }

    public String generateToken(Authentication authentication){
        Instant now = Instant.now();
        // 1. On vérifie et on récupère notre UserPrincipal avec son ID
        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new IllegalArgumentException("Le principal d'authentification n'est pas du type UserPrincipal");
        }

        // 2. On extrait l'ID (qui est un Long)
        Long id = userPrincipal.id();
        String username= userPrincipal.username();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                // Identifiant unique (jti) : permet de révoquer ce jeton à la déconnexion
                .id(UUID.randomUUID().toString())
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpiration, ChronoUnit.HOURS))
                .subject(String.valueOf(id))
                .claim("username", username)
                .build();
        JwtEncoderParameters jwtEncoderParameters =JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}
