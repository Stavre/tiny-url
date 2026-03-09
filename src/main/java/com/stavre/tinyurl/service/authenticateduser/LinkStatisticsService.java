package com.stavre.tinyurl.service.authenticateduser;

import com.stavre.tinyurl.dto.authenticateduser.LinkCountDto;
import com.stavre.tinyurl.repository.authenticateduser.AuthenticatedUserLinkRepository;
import com.stavre.tinyurl.repository.authenticateduser.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LinkStatisticsService {

    private final LinkUserRepository linkUserRepository;
    private final AuthenticatedUserLinkRepository linkRepository;

    public LinkCountDto getLinkCount(String username) {
        long totalLinks = linkUserRepository.countByUserNameIs(username);
        long activeLinks = linkUserRepository.countActiveLinksByUserName(username);
        long expiredLinks = linkUserRepository.countExpiredLinksByUserName(username);

        return new LinkCountDto(totalLinks, activeLinks, expiredLinks);
    }
}
