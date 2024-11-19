package com.sbukak.global.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaMessageListener {
    private SimpMessagingTemplate messagingTemplate;

    @Value("${spring.kafka.consumer.group-id}")
    String groupdId;

    @KafkaListener(topics = "your-topic-name")
    public void listen(String message) {
        // 수신된 메시지를 '/topic/messages'로 브로드캐스트
            messagingTemplate.convertAndSend("/topic/messages", message);
    }
}