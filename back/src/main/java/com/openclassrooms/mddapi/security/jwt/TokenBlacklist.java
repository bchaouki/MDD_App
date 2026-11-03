package com.openclassrooms.mddapi.security.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jetons révoqués à la déconnexion, identifiés par leur "jti".
 * Stockage en mémoire : la liste est vidée au redémarrage du serveur.
 */
@Component
public class TokenBlacklist {

    /** jti -> date d'expiration du jeton. */
    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();

    public void revoke(Jwt jwt) {
        if (jwt.getId() != null) {
            revoked.put(jwt.getId(), jwt.getExpiresAt() != null ? jwt.getExpiresAt() : Instant.MAX);
        }
    }

    public boolean isRevoked(String jti) {
        return jti != null && revoked.containsKey(jti);
    }

    /** Toutes les heures : un jeton expiré est déjà refusé, inutile de le garder. */
    @Scheduled(fixedRate = 3_600_000)
    void purgeExpired() {
        Instant now = Instant.now();
        revoked.values().removeIf(expiresAt -> expiresAt.isBefore(now));
    }
}
