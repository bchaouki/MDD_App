package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;

public record CommentDto(Long id, String content, String authorUsername,  LocalDateTime createdAt) {
}
