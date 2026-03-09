package com.stavre.tinyurl.dto.authenticateduser;

import java.time.LocalDateTime;

public record UpdateLinkRequestDto(String id,
                                   String originalUrl,
                                   String description,
                                   LocalDateTime validFrom,
                                   LocalDateTime validUntil) {
}
