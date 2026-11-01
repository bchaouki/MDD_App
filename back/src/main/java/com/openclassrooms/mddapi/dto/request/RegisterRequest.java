package com.openclassrooms.mddapi.dto.request;

import com.openclassrooms.mddapi.validation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "Nom d'utilisateur", example = "johndoe")
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit centenir entre 3 et 50 caractères")
        String username,

        @Schema(description = "Adresse email", example = "john.doe@mail.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        @Size(max = 255, message = "L'email ne doit pas dépasser 255 caractères")
        String email,

        @Schema(description = "Au moins 8 caractères avec un chiffre, une minuscule, une majuscule et un caractère spécial",
                example = "Password1!")
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Password(message = "Le mot de passe doit centenir au minimum 8 caractères avec: un chiffre, lettre minuscule, lettre majuscule et un caractère spécial")
        String password) {
}
