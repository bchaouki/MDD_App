package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UpdateUserResponse;
import com.openclassrooms.mddapi.dto.response.UserDto;
import com.openclassrooms.mddapi.service.TopicService;
import com.openclassrooms.mddapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "Utilisateur", description = "Profil de l'utilisateur connecté")
@ApiResponse(responseCode = "401", description = "Authentification requise")
public class UserController {

    private final UserService userService;
    private final TopicService topicService;

    @Operation(summary = "Récupérer mon profil")
    @ApiResponse(responseCode = "200", description = "Profil de l'utilisateur connecté")
    @GetMapping
    public ResponseEntity<UserDto> me() {
        UserDto userdto = userService.getCurrentUser();
        return ResponseEntity.ok(userdto);
    }

    @Operation(summary = "Modifier mon profil",
            description = "Le mot de passe est optionnel. Renvoie un nouveau JWT à utiliser pour la suite.")
    @ApiResponse(responseCode = "200", description = "Profil mis à jour")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Email ou nom d'utilisateur déjà utilisé")
    @PutMapping
    public UpdateUserResponse updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return userService.updateCurrentUser(request);
    }

    @Operation(summary = "Lister mes abonnements")
    @ApiResponse(responseCode = "200", description = "Thèmes auxquels l'utilisateur est abonné")
    @GetMapping("/subscriptions")
    public List<TopicDto> subscription() {
        return topicService.findSubscriptions();
    }
}
