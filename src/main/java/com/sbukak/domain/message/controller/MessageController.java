package com.sbukak.domain.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;

@RequiredArgsConstructor
@Controller
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @MessageMapping("/send/all")
    public void sendAllMessage(@Payload String message) {
        kafkaTemplate.send("all-cheer-messages", message);
    }

    @MessageMapping("/send/team/{teamId}")
    public void sendTeamMessage(@Payload String message, @DestinationVariable String teamId) {
        kafkaTemplate.send("team-cheer-messages", teamId, message);
    }
}