package com.sbukak.domain.board.domain;

import com.sbukak.domain.board.dto.CommentDto;
import com.sbukak.domain.board.tmp.User;
import com.sbukak.global.util.Utils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    public Comment(String content, User user, Boolean isAnonymous) {
        this.content = content;
        this.user = user;
        if (isAnonymous != null) {
            this.isAnonymous = isAnonymous;
        }
    }

    public CommentDto toCommentDto() {
        return new CommentDto(
            user.getName(),
            user.getProfileImageUrl(),
            content,
            Utils.dateTimeToChatFormat(createAt)
        );
    }
}
