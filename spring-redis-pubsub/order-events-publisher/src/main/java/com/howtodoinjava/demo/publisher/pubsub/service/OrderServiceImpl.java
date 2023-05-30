package com.howtodoinjava.demo.publisher.pubsub.service;

import com.howtodoinjava.demo.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic channelTopic;

    @Override
    public Long publish(OrderEvent orderEvent){
        log.info("Sending message: {}", orderEvent);
        return redisTemplate.convertAndSend(channelTopic.getTopic(), orderEvent);
    }

}
