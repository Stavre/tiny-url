package com.stavre.tinyurl.authorization;

import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class LinkPermissionsEvaluator implements PermissionEvaluator {

    private final LinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        String username = authentication.getName();
        UUID shortLinkId = UUID.fromString((String) targetId);

        boolean isLinkMissing = isLinkMissing(shortLinkId);
        if (isLinkMissing) {
            return false;
        }

        boolean isLinkOwnedByUser = isLinkOwnedByAuthenticatedUser(shortLinkId, username);
        if (!isLinkOwnedByUser) {
            return false;
        }

        return userHasExpectedRole(authentication, (String) permission);
    }

    private boolean isLinkMissing(UUID shortLinkId) {
        return linkRepository.findLinkByShortLinkId(shortLinkId).isEmpty();
    }

    private boolean isLinkOwnedByAuthenticatedUser(UUID shortLinkId, String username) {
        Optional<LinkUser> linkUser = linkUserRepository.findLinkUserByUserNameAndShortLinkId(username, shortLinkId);
        return linkUser.isPresent();
    }

    private boolean userHasExpectedRole(Authentication authentication, String role) {
        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), role));
    }
}
