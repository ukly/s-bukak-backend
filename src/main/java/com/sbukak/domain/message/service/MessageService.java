package com.sbukak.domain.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.message.domain.Message;
import com.sbukak.domain.message.dto.MessageRequestDTO;
import com.sbukak.domain.message.dto.MessageResponseDTO;
import com.sbukak.domain.message.repository.MessageRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.util.Utils;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<MessageResponseDTO> getMessagesByTeamId(Long teamId) {
        List<Message> messages = messageRepository.findByTeamIdOrNull(teamId);
        messages.forEach(message -> Hibernate.initialize(message.getUser())); // Lazy 로딩 초기화
        return messages.stream()
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        message.getUser().getId(),
                        message.isAnonymous() ? "익명" : message.getUser().getName(),
                        message.isAnonymous() ? null : message.getUser().getProfileImageUrl(),
                        message.getContent(),
                        Utils.dateTimeToChatFormat(message.getCreateAt()),
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

    @Transactional
    public void deleteMessage(Long messageId, User user) {
        Message message = messageRepository.findByIdWithUserAndTeam(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다"));

        if (!message.getUser().equals(user) && !(user.getTeam().getId().equals(message.getTeam().getId()) && user.getRole().equals(ROLE.TEAM))){
            throw new SecurityException("메시지를 삭제할 권한이 없습니다.");
        }

        messageRepository.delete(message);
    }
}