package com.sbukak.domain.message.dto;


public record MessageRequestDTO(
        Long userId,
        String content,
        Long teamId,
        boolean isAnonymous
) {
}
