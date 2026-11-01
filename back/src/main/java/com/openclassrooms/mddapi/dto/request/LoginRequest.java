package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Email ou nom d'utilisateur", example = "john.doe@mail.com")
        @NotBlank(message="L'email ou le nom d'utilisateur est obligatoire")
        String identifier,

        @Schema(description = "Mot de passe", example = "Password1!")
        @NotBlank(message ="Le mot de passe est obligatoire")
        String password) {
}
