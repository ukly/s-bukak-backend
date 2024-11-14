package com.sbukak.domain.bet.dto;

public record SubmitBetRequestDto(
    Long scheduleId,
    Boolean isBetHomeTeam
) {
}
