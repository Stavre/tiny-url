package com.stavre.tinyurl.service.authenticateduser;

import com.stavre.tinyurl.entity.authenticateduser.LinkAccessEntity;
import com.stavre.tinyurl.repository.authenticateduser.LinkAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LinkAccessService {

    private final LinkAccessRepository linkAccessRepository;

    public void recordAccess(String shortUrl) {
        LinkAccessEntity accessLog = new LinkAccessEntity();
        accessLog.setAccessedAt(LocalDateTime.now());
        accessLog.setLinkId(UUID.fromString(shortUrl));
        linkAccessRepository.save(accessLog);
    }
}
