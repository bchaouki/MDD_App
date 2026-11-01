package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;

public record UserDto(Long id,String username, String email, LocalDateTime createdAt) {
}
