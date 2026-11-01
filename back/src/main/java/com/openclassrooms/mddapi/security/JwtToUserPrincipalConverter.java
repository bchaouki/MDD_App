package com.openclassrooms.mddapi.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class JwtToUserPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. On extrait l'ID (le subject) et on le retransforme en Long
        Long id = Long.valueOf(jwt.getSubject());

        // 2. On extrait l'username (si vous l'avez mis dans les claims)
        String username = jwt.getClaimAsString("username");

        // 3. On recrée notre UserPrincipal (pas besoin de mot de passe ici, le token est déjà validé)
        UserPrincipal principal = new UserPrincipal(id, username, "");

        // 4. On retourne le token d'authentification officiel de Spring
        return new UsernamePasswordAuthenticationToken(principal, null, List.of());
    }
}
