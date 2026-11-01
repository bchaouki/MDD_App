package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleDto;
import com.openclassrooms.mddapi.dto.response.ArticleSummaryDto;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.ArticleMapper;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import com.openclassrooms.mddapi.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional(readOnly = true)
    public ArticleDto findById(Long id) {

        return articleRepository.findDetailedById(id)
                .map(articleMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Article introuvable"));
    }

    @Override
    @Transactional
    public ArticleDto create(CreateArticleRequest request) {
        Article article = articleMapper.toEntity(request);
        article.setTitle(article.getTitle().trim());
        article.setTopic(topicRepository.findById(request.topicId())
                .orElseThrow(() -> new NotFoundException("Thème introuvable")));
        article.setAuthor(currentUserProvider.getCurrentUserById());
        return articleMapper.toDto(articleRepository.save(article));
    }

    @Override
    @Transactional
    public List<ArticleSummaryDto> getFeed(Sort.Direction direction) {
     Sort sort =Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        return articleMapper.toSummaryDtos(articleRepository.findFeed(currentUserProvider.getCurrentUserId(), sort));
    }


}
