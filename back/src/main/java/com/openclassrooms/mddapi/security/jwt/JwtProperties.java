package com.openclassrooms.mddapi.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mdd.jwt")
public record JwtProperties(String jwtSecret,  long jwtExpiration) {
}
