package com.sbukak.domain.team.domain;

import com.sbukak.global.converter.GameResultTypeConverter;
import com.sbukak.global.enums.GameResultType;
import com.sbukak.global.enums.SportType;
import com.sbukak.domain.team.dto.TeamDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "name", nullable = false)
    private String name;    //팀 이름

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;    //종목 타입

    @Column(name = "ranking", nullable = false)
    private int ranking;    //순위

    @Column(name = "points", nullable = false)
    private int points;    //승점

    @Column(name = "wins", nullable = false)
    private int wins;    //승

    @Column(name = "draws", nullable = false)
    private int draws;    //무

    @Column(name = "losses", nullable = false)
    private int losses;    //패

    @Column(name = "matches", nullable = false)
    private int matches;    //경기수

    @Column(name = "goals", nullable = false)
    private int goals;    //득점

    @Column(name = "goals_difference", nullable = false)
    private int goalsDifference;    //골득실

    @Convert(converter = GameResultTypeConverter.class)
    @Column(name = "recent_matches", nullable = false)
    private List<GameResultType> recentMatches;    //최근전적

    @Column(name = "icon_image_url", nullable = false)
    private String iconImageUrl;    //아이콘 이미지 url

    @Column(name = "formation_image_url", nullable = false)
    private String formationImageUrl;    //포메이션 이미지 url

    @Builder
    public Team(
        Long id,
        String name,
        SportType sportType,
        int ranking,
        int points,
        int wins,
        int draws,
        int losses,
        int matches,
        int goals,
        int goalsDifference,
        List<GameResultType> recentMatches,
        String iconImageUrl,
        String formationImageUrl
    ) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.ranking = ranking;
        this.points = points;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.matches = matches;
        this.goals = goals;
        this.goalsDifference = goalsDifference;
        this.recentMatches = recentMatches;
        this.iconImageUrl = iconImageUrl;
        this.formationImageUrl = formationImageUrl;
    }

    public TeamDto toTeamDto() {
        return new TeamDto(
            id,
            ranking,
            college.getLeague().getName(),
            college.getName(),
            name,
            points,
            wins,
            sportType == SportType.SOCCER ? draws : null,
            losses,
            matches,
            sportType == SportType.SOCCER ? goals : null,
            goalsDifference,
            recentMatches.stream().map(Enum::ordinal).toList(),
            iconImageUrl,
            formationImageUrl
        );
    }
}