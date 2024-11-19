package com.sbukak.domain.message.service;

import com.sbukak.domain.message.domain.Message;
import com.sbukak.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageListener {

    private final MessageRepository messageRepository;

    @KafkaListener(topics = "team-cheer-messages", groupId = "cheer-group")
    public void listenTeamMessages(String messageJson) {
        // 메시지 수신
        System.out.println("Received message: " + messageJson);

        // 비즈니스 로직 처리 예제
        processAndSaveMessage(messageJson);
    }

    private void processAndSaveMessage(String messageJson) {
        try {
            // 메시지를 저장하거나 다른 비즈니스 로직을 수행
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 필요한 오류 처리 로직 작성
        }
    }
}
