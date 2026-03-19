package com.stavre.tinyurl.dto;

import java.time.LocalDateTime;

public record UpdateLinkRequestDto(
        String originalUrl,
        String description,
        LocalDateTime activeFrom,
        LocalDateTime activeUntil,
        LocalDateTime removeAt) {
}
