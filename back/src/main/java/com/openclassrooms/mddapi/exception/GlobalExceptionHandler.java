package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailAlreadyUsedException.class, UsernameAlreadyUsedException.class})
    public ResponseEntity<Map<String, String>> handleRegistrationConflicts(RuntimeException ex) {
        Map<String, String> errors = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /** Ressource absente (article, thème...) : 404 au lieu d'une erreur 500. */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Email ou nom d'utilisateur déjà pris lors d'une modification du profil. */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status, message));
    }
}
