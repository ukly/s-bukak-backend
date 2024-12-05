package com.sbukak.domain.message.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.message.domain.Message;
import com.sbukak.domain.message.dto.MessageResponseDTO;
import com.sbukak.domain.message.dto.MessageRequestDTO;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

            boolean isFiltered = isFiltered(requestDTO.content());

            Message message;
            if(requestDTO.teamId() == 0) message = messageService.createMessage(requestDTO, user, null, isFiltered);
            else {
                Team team= teamRepository.findById(requestDTO.teamId())
                        .orElseThrow(() -> new IllegalArgumentException("invalid teamId: " + requestDTO.teamId()));
                message = messageService.createMessage(requestDTO, user, team, isFiltered);
            }

            MessageResponseDTO responseDTO = new MessageResponseDTO(
                    message.getId(),
                    user.getId(),
                    user.getName(),
                    user.getProfileImageUrl(),
                    message.getContent(),
                    Utils.dateTimeToChatFormat(message.getCreateAt()),
                    message.isAnonymous(),
                    message.isHidden()
            );

            // WebSocket을 통해 클라이언트로 브로드캐스트
            messagingTemplate.convertAndSend("/topic/cheer-messages-" + requestDTO.teamId(), responseDTO);
            log.info("Message broadcasted to WebSocket: {}", message);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }

    private boolean isFiltered(String content) {
        String url = "https://hun07axt8g.execute-api.us-west-2.amazonaws.com/badword_filtering";
        WebClient webClient = WebClient.create();
        ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

        try {
            // WebClient를 동기 방식으로 사용
            String response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(String.format("{\"text\":\"%s\"}", content))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // block()을 사용하여 동기적으로 결과를 반환

            // JSON 파싱
            JsonNode jsonResponse = objectMapper.readTree(response);
            String prediction = jsonResponse.get("prediction").asText(); // "prediction" 값 읽기

            // "prediction" 값에 따라 true/false 반환
            return "욕설".equals(prediction); // 비속어이면 true, 아니면 false

        } catch (Exception e) {
            e.printStackTrace();
            return false; // 오류 발생 시 기본값
        }
    }
}