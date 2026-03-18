package com.stavre.tinyurl.entity.authenticateduser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Entity
@Data
@Builder
@AllArgsConstructor
public class AuthenticatedUserLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID shortLinkId;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private String description;
    private boolean markedForDeletion;
    private boolean isLatest;
}
