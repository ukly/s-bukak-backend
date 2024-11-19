package com.sbukak.domain.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.message.dto.MessageSendKafkaDTO;
import com.sbukak.domain.message.dto.MessageSendRequestDTO;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.oauth2.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;

@RequiredArgsConstructor
@Controller
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @MessageMapping("/send/all")
    public void sendAllMessage(MessageSendRequestDTO messageDTO, @AuthenticationPrincipal CustomOAuth2User customUser) {
        User user = userRepository.findByEmail(customUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email error in message"));
        String userName = messageDTO.isAnonymous() ? "익명" : user.getName();

        // Kafka 전송용 DTO 생성
        MessageSendKafkaDTO kafkaMessageDTO = new MessageSendKafkaDTO(
                messageDTO.content(),
                userName,
                null // 전체 메시지인 경우 teamId는 null
        );

        try {
            String jsonMessage = objectMapper.writeValueAsString(kafkaMessageDTO);
            kafkaTemplate.send("all-cheer-messages", jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/send/team/{teamId}")
    public void sendTeamMessage(MessageSendRequestDTO messageDTO, @AuthenticationPrincipal CustomOAuth2User customUser) {
        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(() -> new IllegalArgumentException("Email error in message"));
        String userName = messageDTO.isAnonymous() ? "익명" : user.getName();

        MessageSendKafkaDTO kafkaMessageDTO = new MessageSendKafkaDTO(
                messageDTO.content(),
                userName,
                messageDTO.teamId()
        );

        try {
            String jsonMessage = objectMapper.writeValueAsString(kafkaMessageDTO);
            kafkaTemplate.send("team-cheer-messages", jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}