package com.stavre.tinyurl.service.anonymoususer;

import com.stavre.tinyurl.entity.anonymoususer.AnonymousLinkEntity;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.anonymoususer.AnonymousUserLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AnonymousUserLinkService {

    private final LinkFactory linkFactory;
    private final AnonymousUserLinkRepository linkRepository;

    public AnonymousLinkEntity createAnonymousLink(String url) {
        AnonymousLinkEntity shortLink = linkFactory.createAnonymousLink(url);
        return linkRepository.save(shortLink);
    }

    public Optional<String> getOriginalUrl(String uuid) {
        Optional<AnonymousLinkEntity> existingLink = linkRepository.findLinkById(UUID.fromString(uuid));
        return existingLink.map(AnonymousLinkEntity::getOriginalUrl);
    }
}
