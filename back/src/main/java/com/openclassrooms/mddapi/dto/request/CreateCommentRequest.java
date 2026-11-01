package com.openclassrooms.mddapi.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record CreateCommentRequest(
        @Schema(description = "Contenu du commentaire", example = "Super article, merci !")
        @NotBlank(message = "Le commentaire ne peut pas être vide")
        @Size(max = 2000, message = "Le commentaire ne doit pas dépasser 2 000 caractères")
        String content)  {
}
