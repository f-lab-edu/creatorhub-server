package com.creatorhub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "creation_format",
        uniqueConstraints = @UniqueConstraint(name = "uk_creation_format_code", columnNames = "code")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreationFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private boolean isActive;

    @Builder(access = AccessLevel.PRIVATE)
    private CreationFormat(String code, String name, boolean isActive) {
        this.code = code;
        this.name = name;
        this.isActive = isActive;
    }

    public static CreationFormat create(String code, String name) {
        return CreationFormat.builder()
                .code(code)
                .name(name)
                .isActive(true)
                .build();
    }

    public void deactivate() {
        this.isActive = false;
    }
}
