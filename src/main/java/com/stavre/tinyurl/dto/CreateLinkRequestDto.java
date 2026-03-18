package com.stavre.tinyurl.dto;

import java.time.LocalDateTime;

public record CreateLinkRequestDto(String url,
                                   String description,
                                   LocalDateTime validFrom,
                                   LocalDateTime validUntil) {
}
