package com.stavre.tinyurl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Entity
@Data
@AllArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID shortLinkId;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private LocalDateTime removeAt;
    private String description;

    public void extendActivationPeriod() {
        activeUntil = LocalDateTime.now().plusDays(3);
        removeAt = LocalDateTime.now().plusDays(3);
    }
}
