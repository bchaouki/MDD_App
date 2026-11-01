package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.ArticleMapper;
import com.openclassrooms.mddapi.mapper.ArticleMapperImpl;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    private final ArticleMapper articleMapper = new ArticleMapperImpl();
    private ArticleServiceImpl articleService;
    private User author;
    private Topic topic;

    @BeforeEach
    void setUp() {
        articleService = new ArticleServiceImpl(articleRepository, topicRepository, currentUserProvider, articleMapper);
        author = new User("alice", "alice@mail.com", "hash");
        topic = new Topic();
        topic.setId(3L);
        topic.setTitle("Java");
        topic.setDescription("desc");
    }

    @Test
    void createSetsAuthorAndTopic() {
        when(topicRepository.findById(3L)).thenReturn(Optional.of(topic));
        when(currentUserProvider.getCurrentUserById()).thenReturn(author);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = articleService.create(new CreateArticleRequest(3L, "  Titre ", "Contenu"));

        assertThat(dto.title()).isEqualTo("Titre");
        assertThat(dto.content()).isEqualTo("Contenu");
        assertThat(dto.authorUsername()).isEqualTo("alice");
        assertThat(dto.topicId()).isEqualTo(3L);
        assertThat(dto.topicTitle()).isEqualTo("Java");
    }

    @Test
    void createWithUnknownTopicFails() {
        when(topicRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.create(new CreateArticleRequest(3L, "Titre", "Contenu")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void feedTruncatesLongContent() {
        Article article = new Article();
        article.setTitle("Titre");
        article.setContent("x".repeat(500));
        article.setAuthor(author);
        article.setTopic(topic);
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(articleRepository.findFeed(eq(1L), any(Sort.class))).thenReturn(List.of(article));

        var feed = articleService.getFeed(Sort.Direction.ASC);

        assertThat(feed).singleElement().satisfies(summary -> {
            assertThat(summary.excerpt()).hasSize(ArticleMapper.EXCERPT_LENGTH + 1).endsWith("…");
            assertThat(summary.topicTitle()).isEqualTo("Java");
            assertThat(summary.authorUsername()).isEqualTo("alice");
        });
    }

    @Test
    void findUnknownArticleFails() {
        when(articleRepository.findDetailedById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.findById(5L)).isInstanceOf(NotFoundException.class);
    }
}
