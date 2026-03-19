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
        link.setActiveFrom(LocalDateTime.now());
        link.setActiveUntil(LocalDateTime.now().plusDays(3));
        link.setRemoveAt(LocalDateTime.now().plusDays(3));

        return link;
    }

    public Link createUserLink(CreateLinkRequestDto requestDto) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime newactiveFrom = requestDto.activeFrom() != null ? requestDto.activeFrom() : currentTimestamp;
        LocalDateTime newactiveUntil = requestDto.activeUntil() != null
                ? requestDto.activeUntil() : currentTimestamp.plusDays(5);

        Link link = new Link();

        link.setOriginalUrl(requestDto.url());
        link.setShortLinkId(UUID.randomUUID());
        link.setCreatedAt(currentTimestamp);
        link.setUpdatedAt(null);
        link.setActiveFrom(newactiveFrom);
        link.setActiveUntil(newactiveUntil);
        link.setDescription(requestDto.description());

        return link;
    }
}
