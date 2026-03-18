package com.stavre.tinyurl.factory;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LinkFactory {

    public Link createAnonymousLink(String url) {
        Link link = new Link();

        link.setShortLinkId(UUID.randomUUID());
        link.setOriginalUrl(url);
        link.setCreatedAt(LocalDateTime.now());
        link.setValidFrom(LocalDateTime.now());
        link.setValidUntil(LocalDateTime.now().plusDays(3));

        return link;
    }

    public Link createUserLink(CreateLinkRequestDto requestDto) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime newValidFrom = requestDto.validFrom() != null ? requestDto.validFrom() : currentTimestamp;
        LocalDateTime newValidUntil = requestDto.validUntil() != null
                ? requestDto.validUntil() : currentTimestamp.plusDays(5);

        Link link = new Link();

        link.setOriginalUrl(requestDto.url());
        link.setShortLinkId(UUID.randomUUID());
        link.setCreatedAt(currentTimestamp);
        link.setUpdatedAt(null);
        link.setValidFrom(newValidFrom);
        link.setValidUntil(newValidUntil);
        link.setDescription(requestDto.description());
        link.setMarkedForDeletion(false);

        return link;
    }
}
