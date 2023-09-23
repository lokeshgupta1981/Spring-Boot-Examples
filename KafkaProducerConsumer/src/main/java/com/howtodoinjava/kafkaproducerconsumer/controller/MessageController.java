package com.howtodoinjava.kafkaproducerconsumer.controller;

import com.howtodoinjava.kafkaproducerconsumer.service.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController
{
    private final KafkaProducer kafkaProducer;

    public MessageController(KafkaProducer kafkaProducer)
    {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<String> publish(@RequestBody String message, @PathVariable("id") Long id)
    {
        kafkaProducer.sendMessage(id, message);

        return ResponseEntity.ok("Message Sent Successfully");
    }
}
