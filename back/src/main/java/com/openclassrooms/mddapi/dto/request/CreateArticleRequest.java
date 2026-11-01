package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(

        @Schema(description = "Identifiant du thème", example = "1")
        @NotNull(message = "Le thème est obligatoire")
        Long topicId,

        @Schema(description = "Titre de l'article", example = "Découvrir les signals Angular")
        @NotBlank(message = "Veuillez renseigner le titre de l'article")
        @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
        String title,

        @Schema(description = "Contenu de l'article", example = "Les signals simplifient la gestion de l'état...")
        @NotBlank(message = "Veuillez renseigner le contenu de l'article)")
        @Size(max = 20000, message = "Le contenu ne doit pas dépasser 20 000 caractères")
        String content


) {
}
