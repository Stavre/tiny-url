package com.stavre.tinyurl.service;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;

@RequiredArgsConstructor
public class AnonymousLinkService {

    private final LinkFactory linkFactory;
    private final LinkRepository linkRepository;

    @PreAuthorize("hasRole('ANONYMOUS')")
    public Link createAnonymousLink(CreateLinkRequestDto requestDto) {
        Link shortLink = linkFactory.createAnonymousLink(requestDto.url());
        return saveWithRetry(shortLink);
    }

    public Optional<String> getOriginalUrl(String shortLinkId) {
        return linkRepository.findActiveLinkByShortLinkId(shortLinkId)
                .map(Link::getOriginalUrl);
    }

    private Link saveWithRetry(Link link) {
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                return linkRepository.save(link);
            } catch (DataIntegrityViolationException ex) {
                link.setShortLinkId(ShortCodeGenerator.generate());
            }
        }
        throw new IllegalStateException("Failed to generate a unique short code after 5 attempts");
    }
}
