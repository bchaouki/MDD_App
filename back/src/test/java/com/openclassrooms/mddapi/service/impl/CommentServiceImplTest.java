package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.CommentMapperImpl;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, new CommentMapperImpl(), currentUserProvider,
                articleRepository);
    }

    @Test
    void createSetsAuthorAndArticle() {
        Article article = new Article();
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(currentUserProvider.getCurrentUserById()).thenReturn(new User("bob", "bob@mail.com", "hash"));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = commentService.create(1L, new CreateCommentRequest("Super article"));

        assertThat(dto.content()).isEqualTo("Super article");
        assertThat(dto.authorUsername()).isEqualTo("bob");
        ArgumentCaptor<Comment> saved = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(saved.capture());
        assertThat(saved.getValue().getArticle()).isSameAs(article);
    }

    @Test
    void commentingUnknownArticleFails() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(1L, new CreateCommentRequest("Hello")))
                .isInstanceOf(NotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void findByArticleReturnsMappedComments() {
        Comment comment = new Comment();
        comment.setContent("Bravo");
        comment.setAuthor(new User("bob", "bob@mail.com", "hash"));
        when(commentRepository.findArticleId(1L)).thenReturn(List.of(comment));

        var comments = commentService.findByArticle(1L);

        assertThat(comments).singleElement().satisfies(dto -> {
            assertThat(dto.content()).isEqualTo("Bravo");
            assertThat(dto.authorUsername()).isEqualTo("bob");
        });
    }
}
