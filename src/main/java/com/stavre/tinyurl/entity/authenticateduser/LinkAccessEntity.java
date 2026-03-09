package com.stavre.tinyurl.entity.authenticateduser;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(schema = "authenticated_user", name = "access_log")
public class LinkAccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID linkId;
    private LocalDateTime accessedAt;
}
