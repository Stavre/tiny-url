package com.stavre.tinyurl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class LinkAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID linkId;
    private LocalDateTime accessedAt;
}
