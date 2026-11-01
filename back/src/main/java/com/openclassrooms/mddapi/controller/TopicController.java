package com.openclassrooms.mddapi.controller;


import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Thèmes", description = "Liste des thèmes et abonnements")
@ApiResponse(responseCode = "401", description = "Authentification requise")
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "Lister tous les thèmes",
            description = "Le champ `subscribed` indique si l'utilisateur connecté est abonné.")
    @ApiResponse(responseCode = "200", description = "Liste des thèmes")
    @GetMapping
    List<TopicDto> findAll() {
        return topicService.findAll();
    }

    @Operation(summary = "S'abonner à un thème")
    @ApiResponse(responseCode = "201", description = "Abonnement enregistré")
    @ApiResponse(responseCode = "404", description = "Thème introuvable")
    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@Parameter(description = "Identifiant du thème", example = "1") @PathVariable Long id) {
        topicService.subscribe(id);
    }

    @Operation(summary = "Se désabonner d'un thème")
    @ApiResponse(responseCode = "204", description = "Désabonnement effectué")
    @ApiResponse(responseCode = "404", description = "Thème introuvable")
    @DeleteMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@Parameter(description = "Identifiant du thème", example = "1") @PathVariable Long id) {
        topicService.unsubscribe(id);
    }
}
