package com.sbukak.domain.message.domain;

import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.user.entity.User;
import com.sbukak.global.enums.SportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Builder
    public Message(String content, User user, Team team, boolean isAnonymous, boolean isHidden){
        this.content = content;
        this.user = user;
        this.team = team;
        this.isAnonymous = isAnonymous;
        this.isHidden = isHidden;
    }
}
