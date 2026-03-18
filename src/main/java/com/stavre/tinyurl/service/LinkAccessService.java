package com.stavre.tinyurl.service;

import com.stavre.tinyurl.entity.LinkAccess;
import com.stavre.tinyurl.repository.LinkAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LinkAccessService {

    private final LinkAccessRepository linkAccessRepository;

    public void recordAccess(String shortUrl) {
        LinkAccess accessLog = new LinkAccess();
        accessLog.setAccessedAt(LocalDateTime.now());
        accessLog.setLinkId(UUID.fromString(shortUrl));
        linkAccessRepository.save(accessLog);
    }
}
