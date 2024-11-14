package com.sbukak.domain.board.domain;

import com.sbukak.domain.board.dto.BoardDto;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.user.entity.User;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.util.Utils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "board_type", nullable = false)
    private BoardType boardType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    public Board(
        String title,
        String content,
        BoardType boardType,
        SportType sportType,
        User user
    ) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
        this.sportType = sportType;
        this.user = user;
    }

    public BoardDto toBoardDto() {
        return new BoardDto(
            title,
            content,
            user.getName(),
            user.getProfileImageUrl(),
            Utils.dateTimeToDateFormat(createAt),
            comments.stream().map(Comment::toCommentDto).toList()
        );
    }
}
