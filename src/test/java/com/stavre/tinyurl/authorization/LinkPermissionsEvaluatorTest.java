package com.stavre.tinyurl.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.repository.LinkUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LinkPermissionsEvaluatorTest {

    @Mock
    private LinkUserRepository linkUserRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LinkPermissionsEvaluator evaluator;

    @Test
    void hasPermissionWithDomainObjectAlwaysReturnsFalse() {
        assertThat(evaluator.hasPermission(authentication, new Object(), "anything")).isFalse();
    }

    @Test
    void hasPermissionReturnsTrueWhenOwnerHasMatchingRole() {
        when(authentication.getName()).thenReturn("alice");
        when(linkUserRepository.findLinkUserByUserNameAndShortLinkId("alice", "abc123"))
                .thenReturn(Optional.of(new LinkUser()));
        GrantedAuthority authority = () -> "ROLE_USER";
        when(authentication.getAuthorities()).thenAnswer(_ -> (Collection<GrantedAuthority>) List.of(authority));
        assertThat(evaluator.hasPermission(authentication, "abc123", "link", "ROLE_USER")).isTrue();
    }

    @Test
    void hasPermissionReturnsFalseWhenUserDoesNotOwnLink() {
        when(authentication.getName()).thenReturn("alice");
        when(linkUserRepository.findLinkUserByUserNameAndShortLinkId("alice", "abc123"))
                .thenReturn(Optional.empty());
        assertThat(evaluator.hasPermission(authentication, "abc123", "link", "ROLE_USER")).isFalse();
    }

    @Test
    void hasPermissionReturnsFalseWhenOwnerHasWrongRole() {
        when(authentication.getName()).thenReturn("alice");
        when(linkUserRepository.findLinkUserByUserNameAndShortLinkId("alice", "abc123"))
                .thenReturn(Optional.of(new LinkUser()));
        GrantedAuthority authority = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(_ -> (Collection<GrantedAuthority>) List.of(authority));
        assertThat(evaluator.hasPermission(authentication, "abc123", "link", "ROLE_USER")).isFalse();
    }
}
