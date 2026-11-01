package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.response.ArticleSummaryDto;
import com.openclassrooms.mddapi.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Fil d'actualité", description = "Articles des thèmes suivis")
@ApiResponse(responseCode = "401", description = "Authentification requise")
public class feedController {
    private final ArticleService articleService;

    @Operation(summary = "Récupérer mon fil d'actualité",
            description = "Articles des thèmes auxquels l'utilisateur est abonné, triés par date.")
    @ApiResponse(responseCode = "200", description = "Liste des articles")
    @GetMapping
    public List<ArticleSummaryDto> feed(
            @Parameter(description = "Ordre de tri par date de création",
                    schema = @Schema(allowableValues = {"asc", "desc"}, defaultValue = "desc"))
            @RequestParam(defaultValue = "desc") String sort) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return articleService.getFeed(direction);
    }

}
