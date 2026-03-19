package com.stavre.tinyurl.service;

import com.stavre.tinyurl.dto.LinkCountDto;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LinkStatisticsService {

    private final LinkUserRepository linkUserRepository;

    @PreAuthorize("#username == authentication.principal.username")
    public LinkCountDto getLinkCount(String username) {
        long totalLinks = linkUserRepository.countByUserNameIs(username);
        long activeLinks = linkUserRepository.countActiveLinksByUserName(username);
        long expiredLinks = linkUserRepository.countExpiredLinksByUserName(username);

        return new LinkCountDto(totalLinks, activeLinks, expiredLinks);
    }
}
