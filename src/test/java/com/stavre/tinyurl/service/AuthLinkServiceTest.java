package com.stavre.tinyurl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthLinkServiceTest {

    @Mock
    private LinkFactory linkFactory;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkUserRepository linkUserRepository;

    @InjectMocks
    private AuthLinkService authLinkService;

    @Test
    void createUserLinkSavesLinkAndAssociatesLinkUser() {
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", "desc", null, null);
        Link link = new Link();
        link.setShortLinkId("abc123");
        when(linkFactory.createUserLink(dto)).thenReturn(link);
        when(linkRepository.save(link)).thenReturn(link);
        authLinkService.createUserLink("john", dto);
        ArgumentCaptor<LinkUser> captor = ArgumentCaptor.forClass(LinkUser.class);
        verify(linkUserRepository).save(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("john");
        assertThat(captor.getValue().getShortLinkId()).isEqualTo("abc123");
    }

    @Test
    void getLinkForEditReturnsPresentOptionalWhenFound() {
        Link link = new Link();
        when(linkRepository.findLinkByShortLinkId("abc")).thenReturn(Optional.of(link));
        Optional<Link> result = authLinkService.getLinkForEdit("abc");
        assertThat(result).isPresent();
    }

    @Test
    void getLinkForEditReturnsEmptyWhenNotFound() {
        when(linkRepository.findLinkByShortLinkId("missing")).thenReturn(Optional.empty());
        Optional<Link> result = authLinkService.getLinkForEdit("missing");
        assertThat(result).isEmpty();
    }

    @Test
    void updateUserLinkUpdatesAllFieldsAndSaves() {
        Link link = new Link();
        link.setShortLinkId("abc");
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime until = LocalDateTime.now().plusDays(1);
        UpdateLinkRequestDto dto = new UpdateLinkRequestDto("https://new.com", "new desc", from, until);
        when(linkRepository.findLinkByShortLinkId("abc")).thenReturn(Optional.of(link));
        when(linkRepository.save(link)).thenReturn(link);
        Optional<Link> result = authLinkService.updateUserLink("abc", dto);
        assertThat(result).isPresent();
        assertThat(link.getOriginalUrl()).isEqualTo("https://new.com");
        assertThat(link.getDescription()).isEqualTo("new desc");
        assertThat(link.getActiveFrom()).isEqualTo(from);
        assertThat(link.getActiveUntil()).isEqualTo(until);
    }

    @Test
    void updateUserLinkReturnsEmptyWhenLinkNotFound() {
        UpdateLinkRequestDto dto = new UpdateLinkRequestDto("https://new.com", null, null, null);
        when(linkRepository.findLinkByShortLinkId("missing")).thenReturn(Optional.empty());
        Optional<Link> result = authLinkService.updateUserLink("missing", dto);
        assertThat(result).isEmpty();
    }

    @Test
    void deleteUserLinkDeletesBothLinkAndLinkUser() {
        authLinkService.deleteUserLink("abc");
        verify(linkUserRepository).deleteLinkUserEntityByShortLinkId("abc");
        verify(linkRepository).deleteAllByShortLinkId("abc");
    }

    @Test
    void getUserLinksReturnsMappedList() {
        when(linkRepository.findUserLinks("john")).thenReturn(List.of(new Link(), new Link()));
        List<Link> result = authLinkService.getUserLinks("john");
        assertThat(result).hasSize(2);
    }

    @Test
    void saveWithRetryRetriesOnCollisionAndSucceeds() {
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        Link link = new Link();
        when(linkFactory.createUserLink(dto)).thenReturn(link);
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("dup"))
                .thenReturn(link);
        authLinkService.createUserLink("john", dto);
        verify(linkRepository, times(2)).save(any(Link.class));
    }

    @Test
    void saveWithRetryThrowsIllegalStateAfterFiveFailures() {
        Link link = new Link();
        when(linkFactory.createUserLink(any())).thenReturn(link);
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("dup"));
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        assertThatThrownBy(() -> authLinkService.createUserLink("john", dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("5 attempts");
        verify(linkRepository, times(5)).save(any(Link.class));
    }
}
