package com.stavre.tinyurl.service;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import com.stavre.tinyurl.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthLinkService {

    private final LinkFactory linkFactory;
    private final LinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;

    @Transactional
    @PreAuthorize("#username == authentication.principal.username")
    public Link createUserLink(String username, CreateLinkRequestDto requestDto) {
        Link shortLink = linkFactory.createUserLink(requestDto);
        Link link = saveWithRetry(shortLink);

        LinkUser linkUser = new LinkUser();
        linkUser.setUserName(username);
        linkUser.setShortLinkId(link.getShortLinkId());
        linkUserRepository.save(linkUser);

        return link;
    }

    @PreAuthorize("hasPermission(#linkId, 'updateRequest', 'ROLE_USER')")
    public Optional<Link> getLinkForEdit(String linkId) {
        return linkRepository.findLinkByShortLinkId(linkId);
    }

    @Transactional
    @PreAuthorize("hasPermission(#linkId, 'updateRequest', 'ROLE_USER')")
    public Optional<Link> updateUserLink(String linkId, UpdateLinkRequestDto requestDto) {
        Optional<Link> linkOptional = linkRepository.findLinkByShortLinkId(linkId);

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
        linkUserRepository.deleteLinkUserEntityByShortLinkId(linkId);
        linkRepository.deleteAllByShortLinkId(linkId);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public List<Link> getUserLinks(String username) {
        return linkRepository.findUserLinks(username);
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
