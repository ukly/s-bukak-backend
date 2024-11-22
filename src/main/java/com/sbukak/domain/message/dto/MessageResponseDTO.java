package com.sbukak.domain.message.dto;

public record MessageResponseDTO(
        Long id,             // 메시지 ID
        Long userId,         // 유저 ID
        String username,     // 유저 이름 (익명일 경우 "익명")
        String userImage,    // 유저 프로필 이미지 경로
        String content,      // 메시지 내용 (필터링된 내용)
        String createdAt,    // 메시지 생성 시간 (포맷 적용된 문자열)
        boolean isAnonymous, // 익명 여부
        boolean isHidden     // 클린봇 필터링 여부
) {}