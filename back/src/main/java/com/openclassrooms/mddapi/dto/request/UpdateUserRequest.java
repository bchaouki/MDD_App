package com.openclassrooms.mddapi.dto.request;

import com.openclassrooms.mddapi.validation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Schema(description = "Nom d'utilisateur", example = "johndoe")
        @NotBlank (message="Le nom d'utilisateur est obligatoire")
        @Size(min =3, max=50, message= "Le nom d'utilisateur doit centenir entre 3 et 50 caractères")
         String username,

        @Schema(description = "Adresse email", example = "john.doe@mail.com")
        @NotBlank(message="L'email est obligatoire")
        @Email(message= "L'email n'est pas valide")
        @Size(max = 255, message="L'email ne doit pas dépasser 255 caractères")
        String email,

        @Schema(description = "Nouveau mot de passe (optionnel)", example = "NewPassword1!",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Password
        String password) {
}
