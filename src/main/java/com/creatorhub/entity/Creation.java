package com.creatorhub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "creation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE creation SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Creation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creation_format_id", nullable = false)
    private CreationFormat creationFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 400)
    private String plot;

    @Column(nullable = false)
    private boolean isPublic;

    @OneToMany(mappedBy = "creation", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CreationThumbnail> creationThumbnail = new ArrayList<>();

    @OneToMany(mappedBy = "creation", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CreationHashtag> creationHashtags = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Creation(Creator creator,
                     CreationFormat creationFormat,
                     Genre genre,
                     String title,
                     String plot,
                     boolean isPublic) {
        this.creator = creator;
        this.creationFormat = creationFormat;
        this.genre = genre;
        this.title = title;
        this.plot = plot;
        this.isPublic = isPublic;
    }

    public static Creation create(Creator creator,
                                  CreationFormat creationFormat,
                                  Genre genre,
                                  String title,
                                  String plot,
                                  Boolean isPublic) {
        return Creation.builder()
                .creator(creator)
                .creationFormat(creationFormat)
                .genre(genre)
                .title(title)
                .plot(plot)
                .isPublic(isPublic)
                .build();
    }

    public void publish() {
        this.isPublic = true;
    }

    public void unpublish() {
        this.isPublic = false;
    }

}