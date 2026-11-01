package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.Topic;


import java.util.List;

public interface TopicService {
    List<TopicDto> findAll();

    List<TopicDto> findSubscriptions();

    void subscribe(Long topicId);

    void unsubscribe(Long topicId);


}
