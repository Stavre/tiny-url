package com.stavre.tinyurl.dto;

import java.time.LocalDateTime;

public record CreateLinkRequestDto(String url,
                                   String description,
                                   LocalDateTime activeFrom,
                                   LocalDateTime activeUntil) {
}
