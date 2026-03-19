package com.stavre.tinyurl.service;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.factory.LinkUserFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LinkService {

    private final LinkFactory linkFactory;
    private final LinkUserFactory linkUserFactory;

    private final LinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;

    @PreAuthorize("hasRole('ANONYMOUS')")
    public Link createAnonymousLink(CreateLinkRequestDto requestDto) {
        Optional<Link> dbLink = linkRepository.findAnonymousLinkByOriginalUrl(requestDto.url());
        if (dbLink.isPresent()) {
            Link link = dbLink.get();
            link.extendActivationPeriod();
            return linkRepository.save(link);
        }
        Link shortLink = linkFactory.createAnonymousLink(requestDto.url());
        return linkRepository.save(shortLink);
    }

    @Transactional
    @PreAuthorize("#username == authentication.principal.username")
    public Link createUserLink(String username, CreateLinkRequestDto requestDto) {
        Link shortLink = linkFactory.createUserLink(requestDto);
        Link link = linkRepository.save(shortLink);

        LinkUser linkUser = linkUserFactory.createLinkUser(username, link);
        linkUserRepository.save(linkUser);

        return link;
    }

    @PreAuthorize("hasPermission(#linkId, 'updateRequest', 'ROLE_USER')")
    public Optional<Link> updateUserLink(String linkId, UpdateLinkRequestDto requestDto) {
        Optional<Link> linkOptional = linkRepository
                .findLinkByShortLinkId(UUID.fromString(linkId));

        if (linkOptional.isEmpty()) {
            return Optional.empty();
        }

        Link linkEntity = linkOptional.get();

        linkEntity.setActiveFrom(requestDto.activeFrom());
        linkEntity.setActiveUntil(requestDto.activeUntil());
        linkEntity.setOriginalUrl(requestDto.originalUrl());
        linkEntity.setDescription(requestDto.description());

        return Optional.of(linkRepository.save(linkEntity));
    }

    @Transactional
    @PreAuthorize("hasPermission(#linkId, 'deleteRequest', 'ROLE_USER')")
    public void deleteUserLink(String linkId) {
        UUID id = UUID.fromString(linkId);

        linkUserRepository.deleteLinkUserEntityByShortLinkId(id);
        linkRepository.deleteAllByShortLinkId(id);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public List<Link> getUserLinks(String username) {
        return linkRepository.findUserLinks(username);
    }

    public Optional<String> getOriginalUrl(String uuid) {
        Optional<Link> existingLink = linkRepository.findLinkByShortLinkId(UUID.fromString(uuid));

        if (existingLink.isEmpty()) {
            return Optional.empty();
        }

        Link linkEntity = existingLink.get();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(linkEntity.getActiveFrom())) {
            return Optional.empty();
        }
        if (now.isAfter(linkEntity.getActiveUntil())) {
            return Optional.empty();
        }

        return Optional.of(linkEntity.getOriginalUrl());
    }
}