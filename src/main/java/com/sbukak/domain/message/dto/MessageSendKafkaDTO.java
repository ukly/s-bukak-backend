package com.sbukak.domain.message.dto;

// Kafka로 전송할 최종 메시지 구조를 정의한 DTO
public record MessageSendKafkaDTO(
        String content,    // 메시지 내용
        String userName,   // 사용자 이름 (익명 설정에 따라 다름)
        Long teamId      // 팀 ID (전체 메시지인 경우 null)
) {}
