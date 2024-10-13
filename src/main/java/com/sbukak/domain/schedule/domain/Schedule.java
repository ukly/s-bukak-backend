package com.sbukak.domain.schedule.domain;

import com.sbukak.global.enums.SportType;
import com.sbukak.domain.schedule.dto.ScheduleDto;
import com.sbukak.domain.schedule.enums.LeagueType;
import com.sbukak.domain.schedule.tmp.BetType;
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

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
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

    @Column(name = "bet_type", nullable = false)
    private BetType betType;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "league_type", nullable = false)
    private LeagueType leagueType;

    public ScheduleDto toScheduleDto() {
        return new ScheduleDto(
            Utils.dateTimeToKoreanDate(startAt),
            Utils.dateTimeToTime(startAt),
            startAt,
            leagueType.name(),
            sportType.getName(),
            betType,
            homeTeam.getName(),
            homeTeam.getIconImageUrl(),
            awayTeam.getName(),
            awayTeam.getIconImageUrl(),
            sportType.getPlace()
        );
    }
}
