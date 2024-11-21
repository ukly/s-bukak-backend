package com.sbukak.domain.schedule.domain;

import com.sbukak.domain.bet.enums.BetTimeType;
import com.sbukak.global.enums.SportType;
import com.sbukak.domain.schedule.dto.ScheduleDto;
import com.sbukak.domain.schedule.enums.LeagueType;
import com.sbukak.domain.bet.enums.BetType;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.global.util.Utils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "home_team_bet", nullable = false)
    private int homeTeamBet;

    @Column(name = "away_team_bet", nullable = false)
    private int awayTeamBet;

    @Column(name = "home_team_goals", nullable = false)
    private int homeTeamGoals;

    @Column(name = "away_team_goals", nullable = false)
    private int awayTeamGoals;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "league_type", nullable = false)
    private LeagueType leagueType;

    public ScheduleDto toScheduleDto(boolean isParticipated, Boolean isBetHomeTeam) {
        BetTimeType betTimeType = BetTimeType.getBetTimeType(startAt);
        int[] probabilities = calculateWinProbabilities(betTimeType);
        return new ScheduleDto(
            id,
            Utils.dateTimeToKoreanDate(startAt),
            Utils.dateTimeToTime(startAt),
            Utils.dateTimeToFormat(startAt),
            startAt,
            leagueType.name(),
            sportType.getName(),
            BetType.getBetType(startAt, isParticipated),
            betTimeType,
            probabilities != null ? probabilities[0] : null,
            probabilities != null ? probabilities[1] : null,
            homeTeamGoals,
            awayTeamGoals,
            homeTeam.getName(),
            homeTeam.getIconImageUrl(),
            awayTeam.getName(),
            awayTeam.getIconImageUrl(),
            sportType.getPlace(),
            isBetHomeTeam
        );
    }

    public boolean isScheduleFinished() {
        return LocalDateTime.now().isAfter(startAt.plusHours(1));
    }

    private int[] calculateWinProbabilities(BetTimeType betTimeType) {
        if (betTimeType == BetTimeType.예측예정) {
            return null;
        }
        int totalBet = homeTeamBet + awayTeamBet;

        int homeWinProbability = (homeTeamBet * 100) / totalBet;
        int awayWinProbability = 100 - homeWinProbability;

        return new int[]{homeWinProbability, awayWinProbability};
    }
}