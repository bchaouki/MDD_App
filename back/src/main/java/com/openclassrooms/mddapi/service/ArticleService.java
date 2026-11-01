package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleDto;
import com.openclassrooms.mddapi.dto.response.ArticleSummaryDto;
import com.openclassrooms.mddapi.model.Article;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ArticleService {




    ArticleDto findById(Long id);

    ArticleDto create(CreateArticleRequest request);

    List<ArticleSummaryDto> getFeed(Sort.Direction direction);
}
