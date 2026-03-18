package com.stavre.tinyurl.factory;

import com.stavre.tinyurl.dto.authenticateduser.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.anonymoususer.AnonymousLinkEntity;
import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LinkFactory {

    public AnonymousLinkEntity createAnonymousLink(String originalUrl) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        AnonymousLinkEntity link = new AnonymousLinkEntity();

        link.setOriginalUrl(originalUrl);
        link.setCreatedAt(currentTimestamp);
        link.setUpdatedAt(null);
        link.setValidFrom(currentTimestamp);
        link.setValidUntil(currentTimestamp.plusDays(3));

        return link;
    }

    public AuthenticatedUserLinkEntity createUserLink(CreateLinkRequestDto requestDto) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime newValidFrom = requestDto.validFrom() != null ? requestDto.validFrom() : currentTimestamp;
        LocalDateTime newValidUntil = requestDto.validUntil() != null
                ? requestDto.validUntil() : currentTimestamp.plusDays(5);
        AuthenticatedUserLinkEntity link = new AuthenticatedUserLinkEntity();

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
