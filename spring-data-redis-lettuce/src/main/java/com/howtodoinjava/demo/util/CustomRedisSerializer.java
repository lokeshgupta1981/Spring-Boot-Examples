package com.howtodoinjava.demo.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

@Slf4j
public class CustomRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper mapper;

    public CustomRedisSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException while compressing value to redis {}",
                    e.getMessage(), e);
        }
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
}
