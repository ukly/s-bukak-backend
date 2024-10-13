package com.sbukak.domain.schedule.dto;

import com.sbukak.domain.schedule.tmp.BetType;

import java.time.LocalDateTime;

public record ScheduleDto(
    String startDate,  //05월 03일 (금)
    String startTime,   //17:00
    LocalDateTime startAt,
    String leagueName,  //성곡리그,
    String sportType,   //축구
    BetType betType,
    String homeTeamName,
    String homeTeamIconImageUrl,
    String awayTeamName,
    String awayTeamIconImageUrl,
    String place    //국민대학교 운동장
) {
}
