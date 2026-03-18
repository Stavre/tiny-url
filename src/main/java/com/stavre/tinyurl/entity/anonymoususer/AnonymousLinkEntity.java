package com.stavre.tinyurl.entity.anonymoususer;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Entity
@Data
@Builder
@AllArgsConstructor
public class AnonymousLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
