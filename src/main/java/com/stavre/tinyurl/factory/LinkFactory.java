package com.stavre.tinyurl.factory;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.util.ShortCodeGenerator;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class LinkFactory {

    public Link createAnonymousLink(String url) {
        Link link = new Link();

        link.setShortLinkId(ShortCodeGenerator.generate());
        link.setOriginalUrl(url);
        link.setCreatedAt(LocalDateTime.now());
        link.setActiveFrom(LocalDateTime.now());
        link.setActiveUntil(LocalDateTime.now().plusDays(3));

        return link;
    }

    public Link createUserLink(CreateLinkRequestDto requestDto) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime newActiveFrom = requestDto.activeFrom() != null ? requestDto.activeFrom() : currentTimestamp;
        LocalDateTime newActiveUntil = requestDto.activeUntil() != null
                ? requestDto.activeUntil() : currentTimestamp.plusDays(5);

        Link link = new Link();

        link.setOriginalUrl(requestDto.url());
        link.setShortLinkId(ShortCodeGenerator.generate());
        link.setCreatedAt(currentTimestamp);
        link.setUpdatedAt(null);
        link.setActiveFrom(newActiveFrom);
        link.setActiveUntil(newActiveUntil);
        link.setDescription(requestDto.description());

        return link;
    }
}
