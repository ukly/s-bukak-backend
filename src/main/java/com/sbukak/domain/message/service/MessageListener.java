package com.sbukak.domain.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.message.domain.Message;
import com.sbukak.domain.message.dto.MessageResponseDTO;
import com.sbukak.domain.message.dto.MessageRequestDTO;
import com.sbukak.domain.message.repository.MessageRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener {

    private final SimpMessagingTemplate messagingTemplate; // WebSocket 브로드캐스트를 위한 템플릿
    private final ObjectMapper objectMapper; // JSON 처리를 위한 ObjectMapper
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final TeamRepository teamRepository;

    @KafkaListener(topicPattern = "cheer-messages-.*", groupId = "cheer-group")
    public void listenTeamMessages(String messageJson) {
        log.info("Received message from Kafka: {}", messageJson);

        try {
            // JSON 메시지를 Message 객체로 변환
            MessageRequestDTO requestDTO = objectMapper.readValue(messageJson, MessageRequestDTO.class);

            User user = userRepository.findById(requestDTO.userId()).orElseThrow(
                    () -> new IllegalArgumentException("User not found for ID: " + requestDTO.userId()));

            Message message;
            if(requestDTO.teamId() == 0) message = messageService.createMessage(requestDTO, user, null, false);
            else {
                Team team= teamRepository.findById(requestDTO.teamId())
                        .orElseThrow(() -> new IllegalArgumentException("invalid teamId: " + requestDTO.teamId()));
                message = messageService.createMessage(requestDTO, user, team, false);
            }

            MessageResponseDTO responseDTO = new MessageResponseDTO(
                    message.getId(),
                    user.getId(),
                    user.getName(),
                    user.getProfileImageUrl(),
                    message.getContent(),
                    message.getCreateAt().toString(),
                    message.isAnonymous(),
                    message.isHidden()
            );

            // WebSocket을 통해 클라이언트로 브로드캐스트
            messagingTemplate.convertAndSend("/topic/team-cheer-messages-" + requestDTO.teamId(), responseDTO);
            log.info("Message broadcasted to WebSocket: {}", message);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}