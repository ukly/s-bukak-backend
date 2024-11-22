package com.sbukak.domain.schedule.domain;

import com.sbukak.domain.bet.enums.BetTimeType;
import com.sbukak.domain.bet.enums.BetType;
import com.sbukak.domain.schedule.dto.ScheduleDto;
import com.sbukak.domain.schedule.enums.LeagueType;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.util.Utils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private int homeTeamBet = 0;

    @Column(name = "away_team_bet", nullable = false)
    private int awayTeamBet = 0;

    @Column(name = "home_team_goals", nullable = false)
    private int homeTeamGoals = 0;

    @Column(name = "away_team_goals", nullable = false)
    private int awayTeamGoals = 0;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "league_type", nullable = false)
    private LeagueType leagueType;

    @Column(name = "place")
    private String place;

    @Builder
    public Schedule(
        Team homeTeam,
        Team awayTeam,
        LocalDateTime startAt,
        SportType sportType,
        LeagueType leagueType,
        String place
    ) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startAt = startAt;
        this.sportType = sportType;
        this.leagueType = leagueType;
        this.place = place;
    }

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
            place != null ? "국민대학교 " + place : sportType.getPlace(),
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

    public void update(
        SportType sportType,
        LeagueType leagueType,
        Team homeTeam,
        Team awayTeam,
        LocalDateTime startAt,
        String place
    ) {
        this.sportType = sportType;
        this.leagueType = leagueType;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startAt = startAt;
        this.place = place;
    }
}