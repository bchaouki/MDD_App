package com.openclassrooms.mddapi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(int status, String error, String message, Map<String, String> fields, Instant timestamp) {

    public static ApiError of(HttpStatus status, String message) {
        return of(status, message, null);
    }

    public static ApiError of(HttpStatus status, String message, Map<String, String> fields) {
        return new ApiError(status.value(), status.getReasonPhrase(), message, fields, Instant.now());
    }
}
