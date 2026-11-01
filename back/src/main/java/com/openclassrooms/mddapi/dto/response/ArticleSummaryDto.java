package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;

public record ArticleSummaryDto(Long id, String title, String excerpt, String authorUsername,
                                String topicTitle, LocalDateTime createdAt) {
}
