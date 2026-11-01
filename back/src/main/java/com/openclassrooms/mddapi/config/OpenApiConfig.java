package com.openclassrooms.mddapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Documentation OpenAPI / Swagger.
 * UI : http://localhost:8080/swagger-ui.html — JSON : http://localhost:8080/v3/api-docs
 * Toutes les routes exigent un JWT sauf celles annotées @SecurityRequirements() (vide).
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MDD API",
                version = "0.0.1",
                description = "API REST du réseau social MDD (Monde de Dév)"),
        servers = @Server(url = "http://localhost:8080", description = "Local"),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token obtenu via /api/auth/login ou /api/auth/register")
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
}
