package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleDto;
import com.openclassrooms.mddapi.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Consultation et création d'articles")
@ApiResponse(responseCode = "401", description = "Authentification requise")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Détail d'un article")
    @ApiResponse(responseCode = "200", description = "Article trouvé")
    @ApiResponse(responseCode = "404", description = "Article introuvable")
    @GetMapping("/{id}")
    public ArticleDto findById(@Parameter(description = "Identifiant de l'article", example = "1") @PathVariable Long id) {

        return articleService.findById(id);
    }

    @Operation(summary = "Créer un article", description = "L'auteur est l'utilisateur connecté.")
    @ApiResponse(responseCode = "201", description = "Article créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Thème introuvable")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ArticleDto create(@Valid @RequestBody CreateArticleRequest request) {

        return articleService.create(request);
    }


}
