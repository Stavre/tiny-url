package com.stavre.tinyurl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Entity
@Data
@AllArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String shortLinkId;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private String description;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        if (activeFrom != null && now.isBefore(activeFrom)) {
            return false;
        }
        return activeUntil == null || activeUntil.isAfter(now);
    }

}
