package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.config.OpenApiConfig;
import com.openclassrooms.mddapi.security.jwt.TokenBlacklist;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import com.openclassrooms.mddapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion")
@SecurityRequirements // routes publiques : pas de JWT
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final JwtDecoder jwtDecoder;
    private final TokenBlacklist tokenBlacklist;

    @Operation(summary = "Créer un compte", description = "Inscrit un utilisateur et renvoie un JWT.")
    @ApiResponse(responseCode = "201", description = "Compte créé")
    @ApiResponse(responseCode = "400", description = "Données invalides, email ou nom d'utilisateur déjà utilisé")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Se connecter", description = "Connexion par email ou nom d'utilisateur. Renvoie un JWT.")
    @ApiResponse(responseCode = "200", description = "Connexion réussie")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @Operation(summary = "Se déconnecter",
            description = "Révoque le JWT envoyé : il est refusé ensuite, même s'il n'a pas expiré.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @ApiResponse(responseCode = "204", description = "Déconnexion réussie")
    @ApiResponse(responseCode = "401", description = "Authentification requise")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        // Le jeton a déjà été validé par Spring Security (route authentifiée).
        tokenBlacklist.revoke(jwtDecoder.decode(authorization.substring(BEARER_PREFIX.length())));
    }


}
