package com.stavre.tinyurl.dto;

public record LinkCountDto(long totalLinks, long activeLinks, long expiredLinks) {
}
