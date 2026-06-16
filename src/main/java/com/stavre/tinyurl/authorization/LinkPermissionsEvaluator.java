package com.stavre.tinyurl.authorization;

import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LinkPermissionsEvaluator implements PermissionEvaluator {

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
        String shortLinkId = (String) targetId;

        if (!isLinkOwnedByAuthenticatedUser(shortLinkId, username)) {
            return false;
        }

        return userHasExpectedRole(authentication, (String) permission);
    }

    private boolean isLinkOwnedByAuthenticatedUser(String shortLinkId, String username) {
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
