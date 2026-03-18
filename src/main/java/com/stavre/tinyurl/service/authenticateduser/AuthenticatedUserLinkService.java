package com.stavre.tinyurl.service.authenticateduser;

import com.stavre.tinyurl.dto.authenticateduser.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.authenticateduser.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import com.stavre.tinyurl.entity.authenticateduser.LinkUserEntity;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.authenticateduser.AuthenticatedUserLinkRepository;
import com.stavre.tinyurl.repository.authenticateduser.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthenticatedUserLinkService {

    private final AuthenticatedUserLinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;
    private final LinkFactory linkFactory;

    public AuthenticatedUserLinkEntity createUserLink(CreateLinkRequestDto requestDto) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        AuthenticatedUserLinkEntity shortLink = linkFactory.createUserLink(requestDto);
        AuthenticatedUserLinkEntity link = linkRepository.save(shortLink);

        LinkUserEntity linkUser = new LinkUserEntity();
        linkUser.setUserName(username);
        linkUser.setLinkId(link.getShortLinkId());

        linkUserRepository.save(linkUser);

        return link;

    }

    public Optional<AuthenticatedUserLinkEntity> updateUserLink(UpdateLinkRequestDto requestDto) {
        Optional<AuthenticatedUserLinkEntity> linkOptional = linkRepository
                .findLinkByShortLinkId(UUID.fromString(requestDto.id()));

        if (linkOptional.isEmpty()) {
            return Optional.empty();
        }

        AuthenticatedUserLinkEntity linkEntity = linkOptional.get();

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

    public Optional<String> getOriginalUrl(String uuid) {
        Optional<AuthenticatedUserLinkEntity> existingLink = linkRepository
                .findLinkByShortLinkId(UUID.fromString(uuid));
        return existingLink.map(AuthenticatedUserLinkEntity::getOriginalUrl);
    }

    public List<AuthenticatedUserLinkEntity> getUserLinks(String username) {
        List<UUID> linkIds = linkUserRepository.getLinkUserEntitiesByUserName(username)
                .stream()
                .map(LinkUserEntity::getLinkId)
                .toList();
        return linkRepository.findAllLinksByShortLinkIds(linkIds);
    }
}
