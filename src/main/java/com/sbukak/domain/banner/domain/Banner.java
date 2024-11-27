package com.sbukak.domain.banner.domain;

import com.sbukak.domain.banner.enums.BannerStatusType;
import com.sbukak.domain.user.entity.User;
import com.sbukak.global.enums.SportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "banner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDate endAt;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Builder
    public Banner(String title, User user, SportType sportType, LocalDate startAt, LocalDate endAt, String fileName, String fileUrl) {
        this.title = title;
        this.user = user;
        this.sportType = sportType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public BannerStatusType getBannerStatusType() {
        return BannerStatusType.getBannerStatus(startAt, endAt);
    }
}
