package com.stavre.tinyurl.dto.authenticateduser;

public record LinkCountDto(long totalLinks, long activeLinks, long expiredLinks) {
}
