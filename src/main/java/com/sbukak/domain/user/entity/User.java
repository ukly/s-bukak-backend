package com.sbukak.domain.user.entity;

import com.sbukak.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profileImageUrl", nullable = false)
    private String profileImageUrl;

    @Column(name = "role", nullable = false)
    private ROLE role;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "password")
    private String password;

    @Column(name = "is_registered", nullable = false)
    private boolean isRegistered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public void checkAdmin() {
        if (!isAdmin) {
            throw new IllegalStateException("해당 유저는 어드민이 아닙니다.");
        }
    }

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

}
