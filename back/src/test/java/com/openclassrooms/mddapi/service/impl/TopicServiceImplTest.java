package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.mapper.TopicMapperImpl;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    private final TopicMapper topicMapper = new TopicMapperImpl();
    private TopicServiceImpl topicService;
    private Topic java;
    private Topic angular;

    @BeforeEach
    void setUp() {
        topicService = new TopicServiceImpl(topicRepository, currentUserProvider, topicMapper);
        java = topic(1L, "Java");
        angular = topic(2L, "Angular");
    }

    @Test
    void findAllFlagsSubscribedTopics() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(10L);
        when(topicRepository.findSubscribedByUserId(10L)).thenReturn(List.of(java));
        when(topicRepository.findAllByOrderByTitleAsc()).thenReturn(List.of(angular, java));

        var topics = topicService.findAll();

        assertThat(topics).extracting("title", "subscribed")
                .containsExactly(tuple("Angular", false), tuple("Java", true));
    }

    @Test
    void findSubscriptionsFlagsAllAsSubscribed() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(10L);
        when(topicRepository.findSubscribedByUserId(10L)).thenReturn(List.of(java));

        assertThat(topicService.findSubscriptions()).extracting("title", "subscribed")
                .containsExactly(tuple("Java", true));
    }

    @Test
    void subscribeIsIdempotent() {
        User user = new User("alice", "alice@mail.com", "hash");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(java));
        when(currentUserProvider.getCurrentUserById()).thenReturn(user);

        topicService.subscribe(1L);
        topicService.subscribe(1L);

        assertThat(user.getTopics()).containsExactly(java);
    }

    @Test
    void unsubscribeRemovesTopic() {
        User user = new User("alice", "alice@mail.com", "hash");
        user.getTopics().add(java);
        when(topicRepository.findById(1L)).thenReturn(Optional.of(java));
        when(currentUserProvider.getCurrentUserById()).thenReturn(user);

        topicService.unsubscribe(1L);

        assertThat(user.getTopics()).isEmpty();
    }

    @Test
    void subscribeToUnknownTopicFails() {
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.subscribe(99L)).isInstanceOf(NotFoundException.class);
    }

    private static Topic topic(Long id, String title) {
        Topic topic = new Topic();
        topic.setId(id);
        topic.setTitle(title);
        topic.setDescription(title + " description");
        return topic;
    }
}
