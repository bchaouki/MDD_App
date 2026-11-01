package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;


public record ArticleDto(Long id, String title, String content,
                         String authorUsername, Long topicId, String topicTitle, LocalDateTime createdAt) {
}
