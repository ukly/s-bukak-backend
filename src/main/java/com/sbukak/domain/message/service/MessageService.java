package com.sbukak.domain.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.message.domain.Message;
import com.sbukak.domain.message.dto.MessageRequestDTO;
import com.sbukak.domain.message.dto.MessageResponseDTO;
import com.sbukak.domain.message.repository.MessageRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    public List<MessageResponseDTO> getMessagesByTeamId(Long teamId) {
        List<Message> messages = messageRepository.findByTeamId(teamId);
        return messages.stream()
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        message.getUser().getId(),
                        message.isAnonymous() ? "익명" : message.getUser().getName(),
                        message.isAnonymous() ? null : message.getUser().getProfileImageUrl(),
                        message.getContent(),
                        message.getCreateAt().toString(),
                        message.isAnonymous(),
                        message.isHidden()
                ))
                .collect(Collectors.toList());
    }

    public void sendMessage(MessageRequestDTO requestDTO, String topic) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(requestDTO);
            kafkaTemplate.send(topic, jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message createMessage(MessageRequestDTO requestDTO, User user,
                              Team team, Boolean isHidden){
        Message message = Message.builder().content(requestDTO.content())
                .user(user)
                .team(team)
                .isAnonymous(requestDTO.isAnonymous())
                .isHidden(isHidden)
                .build();

        messageRepository.save(message);

        return message;
    }
}
