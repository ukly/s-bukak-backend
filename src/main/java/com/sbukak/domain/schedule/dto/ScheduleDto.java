package com.sbukak.domain.schedule.dto;

import com.sbukak.domain.bet.enums.BetTimeType;
import com.sbukak.domain.bet.enums.BetType;

import java.time.LocalDateTime;

public record ScheduleDto(
    Long scheduleId,
    String startDate,  //05월 03일 (금)
    String startTime,   //17:00
    String startDatetime,   //2024.03.18 18:00
    LocalDateTime startAt,
    String leagueName,  //성곡리그,
    String sportType,   //축구
    BetType betType,    //미참여
    BetTimeType betTimeType,    //미참여
    Integer homeTeamBetPercentage,
    Integer awayTeamBetPercentage,
    int homeTeamGoals,
    int awayTeamGoals,
    String homeTeamName,
    String homeCollegeName,
    String homeTeamIconImageUrl,
    String awayTeamName,
    String awayCollegeName,
    String awayTeamIconImageUrl,
    String place,    //국민대학교 운동장
    Boolean isBetHomeTeam
) {
}
