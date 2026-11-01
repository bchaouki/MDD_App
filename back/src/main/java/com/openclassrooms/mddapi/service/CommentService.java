package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentDto;

import java.util.List;

public interface CommentService {


    CommentDto create(Long articleId, CreateCommentRequest request);

    List<CommentDto> findByArticle(Long articleId);
}
