package com.sbukak.domain.board.domain;

import com.sbukak.domain.board.dto.CommentDto;
import com.sbukak.domain.user.entity.User;
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

    @Column(name = "content", nullable = false, columnDefinition = "text")
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
    public Comment(String content, User user, Boolean isAnonymous, Board board) {
        this.content = content;
        this.user = user;
        if (isAnonymous != null) {
            this.isAnonymous = isAnonymous;
        }
        this.board = board;
    }

    public CommentDto toCommentDto() {
        return new CommentDto(
            isAnonymous ? "익명" : user.getName(),
            isAnonymous ? "https://sbukak.s3.ap-northeast-2.amazonaws.com/profile_anonymous.png" : user.getProfileImageUrl(),
            content,
            Utils.dateTimeToChatFormat(createAt)
        );
    }
}
