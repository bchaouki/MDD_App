package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import com.openclassrooms.mddapi.service.TopicService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final CurrentUserProvider currentUserProvider;
    private final TopicMapper topicMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findAll() {

        Set<Long> subscribedIds = topicRepository.findSubscribedByUserId(currentUserProvider.getCurrentUserId())
                .stream()
                .map(Topic::getId)
                .collect(Collectors.toSet());
        return topicRepository.findAllByOrderByTitleAsc().stream()
                .map(topic -> topicMapper.toDto(topic, subscribedIds.contains(topic.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findSubscriptions() {
        return topicRepository.findSubscribedByUserId(currentUserProvider.getCurrentUserId()).stream()
                .map(topic -> topicMapper.toDto(topic, true))
                .toList();
    }

    @Override
    @Transactional
    public void subscribe(Long topicId) {
       Topic topic = findTopicById(topicId);
        User user = currentUserProvider.getCurrentUserById();
        user.getTopics().add(topic);

    }

    @Override
    @Transactional
    public void unsubscribe(Long topicId) {
        Topic topic = findTopicById(topicId);
        User user = currentUserProvider.getCurrentUserById();
        user.getTopics().remove(topic);
    }

    private Topic findTopicById(Long topicId) {

        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Thème introuvable"));
    }


}
