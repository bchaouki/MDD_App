package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleDto;
import com.openclassrooms.mddapi.dto.response.ArticleSummaryDto;
import com.openclassrooms.mddapi.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    int EXCERPT_LENGTH = 200;

    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicTitle", source = "topic.title")
    ArticleDto  toDto(Article article);

    @Mapping(target="authorUsername", source="author.username")
    @Mapping(target="topicTitle", source="topic.title")
    @Mapping(target="excerpt", source="content",  qualifiedByName = "excerpt")
    ArticleSummaryDto toSummaryDto(Article article);

    List<ArticleSummaryDto> toSummaryDtos(List<Article> articles);

    @Mapping(target="id", ignore = true)
    @Mapping(target="author", ignore = true)
    @Mapping(target="topic", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Article toEntity(CreateArticleRequest request);

    @Named("excerpt")
    default String excerpt(String content) {
        if (content == null || content.length() <= EXCERPT_LENGTH) {
            return content;
        }
        return content.substring(0, EXCERPT_LENGTH).stripTrailing() + "…";
    }
}
