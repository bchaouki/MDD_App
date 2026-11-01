package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentDto;
import com.openclassrooms.mddapi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@RequiredArgsConstructor
@Tag(name = "Commentaires", description = "Commentaires d'un article")
@ApiResponse(responseCode = "401", description = "Authentification requise")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Lister les commentaires d'un article")
    @ApiResponse(responseCode = "200", description = "Liste des commentaires")
    @GetMapping
    public List<CommentDto> findByArticle(
            @Parameter(description = "Identifiant de l'article", example = "1") @PathVariable("articleId") Long articleId) {
        return commentService.findByArticle(articleId);
    }

    @Operation(summary = "Commenter un article", description = "L'auteur est l'utilisateur connecté.")
    @ApiResponse(responseCode = "201", description = "Commentaire créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Article introuvable")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(
            @Parameter(description = "Identifiant de l'article", example = "1") @PathVariable("articleId") Long articleId,
            @Valid @RequestBody CreateCommentRequest request) {
        return commentService.create(articleId, request);

    }

}
