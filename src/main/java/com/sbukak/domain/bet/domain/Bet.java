package com.sbukak.domain.bet.domain;

import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "is_bet_home_team", nullable = false)
    private Boolean isBetHomeTeam;

    public Bet(User user, Schedule schedule, Boolean isBetHomeTeam) {
        this.user = user;
        this.schedule = schedule;
        this.isBetHomeTeam = isBetHomeTeam;
    }
}