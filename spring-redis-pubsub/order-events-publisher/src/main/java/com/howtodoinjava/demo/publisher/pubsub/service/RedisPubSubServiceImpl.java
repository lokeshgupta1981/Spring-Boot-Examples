package com.howtodoinjava.demo.publisher.pubsub.service;

import com.howtodoinjava.demo.model.OrderEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisPubSubServiceImpl implements RedisPubSubService{

    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic channelTopic;

    @Override
    public void publish(OrderEvents orderEvents){
        log.info("Sending message Sync: {}", orderEvents);
        redisTemplate.convertAndSend(channelTopic.getTopic(), orderEvents);
    }

}
