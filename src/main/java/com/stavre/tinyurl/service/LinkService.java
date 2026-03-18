package com.stavre.tinyurl.service;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LinkService {

    private final LinkFactory linkFactory;
    private final LinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;
    
    public Link createAnonymousLink(CreateLinkRequestDto requestDto) {
        Link shortLink = linkFactory.createAnonymousLink(requestDto.url());
        return linkRepository.save(shortLink);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public Link createUserLink(String username, CreateLinkRequestDto requestDto) {
        Link shortLink = linkFactory.createUserLink(requestDto);
        Link link = linkRepository.save(shortLink);

        LinkUser linkUser = new LinkUser();
        linkUser.setUserName(username);
        linkUser.setLinkId(link.getShortLinkId());

        linkUserRepository.save(linkUser);

        return link;

    }

    public Optional<Link> updateUserLink(UpdateLinkRequestDto requestDto) {
        Optional<Link> linkOptional = linkRepository
                .findLinkByShortLinkId(UUID.fromString(requestDto.id()));

        if (linkOptional.isEmpty()) {
            return Optional.empty();
        }

        Link linkEntity = linkOptional.get();

        linkEntity.setValidFrom(requestDto.validFrom());
        linkEntity.setValidUntil(requestDto.validUntil());
        linkEntity.setOriginalUrl(requestDto.originalUrl());
        linkEntity.setDescription(requestDto.description());

        return Optional.of(linkRepository.save(linkEntity));
    }

    @Transactional
    public void deleteLink(String linkId) {
        UUID id = UUID.fromString(linkId);

        linkUserRepository.deleteLinkUserEntityByLinkId(id);
        linkRepository.deleteAllByShortLinkId(id);
    }

    public List<Link> getUserLinks(String username) {
        List<UUID> linkIds = linkUserRepository.getLinkUserEntitiesByUserName(username)
                .stream()
                .map(LinkUser::getLinkId)
                .toList();
        return linkRepository.findAllLinksByShortLinkIds(linkIds);
    }

    public Optional<String> getOriginalUrl(String uuid) {
        Optional<Link> existingLink = linkRepository.findLinkByShortLinkId(UUID.fromString(uuid));
        return existingLink.map(Link::getOriginalUrl);
    }
}
