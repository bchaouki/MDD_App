package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentDto;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import com.openclassrooms.mddapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CurrentUserProvider  currentUserProvider;
    private final ArticleRepository articleRepository;


    @Override
    @Transactional
    public CommentDto create(Long articleId, CreateCommentRequest request) {
        Comment comment = commentMapper.toEntity(request);
        comment.setAuthor(currentUserProvider.getCurrentUserById());
        comment.setArticle(articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("Article introuvable"))
        );
        return commentMapper.toDto(commentRepository.save(comment)) ;
    }

    @Override
    @Transactional
    public List<CommentDto> findByArticle(Long articleId) {

        return commentMapper.toDtos(commentRepository.findArticleId(articleId));
    }
}
