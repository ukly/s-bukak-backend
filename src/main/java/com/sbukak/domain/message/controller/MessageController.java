package com.sbukak.domain.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MessageController {

    private KafkaTemplate<String, String> kafkaTemplate;

    @MessageMapping("/send")
    public void sendMessage(String message) {
        kafkaTemplate.send("your-topic-name", message);
    }
}