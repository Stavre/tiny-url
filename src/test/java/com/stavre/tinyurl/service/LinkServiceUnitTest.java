package com.stavre.tinyurl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.factory.LinkUserFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "VariableDeclarationUsageDistance"})
@ExtendWith(MockitoExtension.class)
class LinkServiceUnitTest {

    @Mock
    private LinkFactory linkFactory;
    @Mock
    private LinkUserFactory linkUserFactory;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private LinkUserRepository linkUserRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    void createAnonymousLink_whenExistingLink_thenExtendAndSave() {
        CreateLinkRequestDto req = new CreateLinkRequestDto("https://example.com", "", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Link expected = new Link();
        expected.setShortLinkId(UUID.randomUUID());
        expected.setOriginalUrl(req.url());
        // spy on extendActivationPeriod by using a real object or verify save called with same instance
        when(linkRepository.findAnonymousLinkByOriginalUrl(req.url())).thenReturn(Optional.of(expected));
        when(linkRepository.save(expected)).thenReturn(expected);

        Link result = linkService.createAnonymousLink(req);

        verify(linkRepository).findAnonymousLinkByOriginalUrl(req.url());
        verify(linkRepository).save(expected);
        assertThat(result).isSameAs(expected);
    }

    @Test
    void createAnonymousLink_whenNotExisting_thenCreateAndSave() {
        CreateLinkRequestDto req = new CreateLinkRequestDto("https://example.com", "", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        when(linkRepository.findAnonymousLinkByOriginalUrl(req.url())).thenReturn(Optional.empty());

        Link created = new Link();
        created.setShortLinkId(UUID.randomUUID());
        created.setOriginalUrl(req.url());
        when(linkFactory.createAnonymousLink(req.url())).thenReturn(created);
        when(linkRepository.save(created)).thenReturn(created);

        Link result = linkService.createAnonymousLink(req);

        verify(linkFactory).createAnonymousLink(req.url());
        verify(linkRepository).save(created);
        assertThat(result).isSameAs(created);
    }

    @Test
    void createUserLink_savesLinkAndLinkUser() {
        CreateLinkRequestDto req = new CreateLinkRequestDto("https://example.com", "", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Link created = new Link();
        created.setShortLinkId(UUID.randomUUID());
        when(linkFactory.createUserLink(req)).thenReturn(created);
        when(linkRepository.save(created)).thenReturn(created);

        LinkUser lu = new LinkUser();
        String username = "alice";
        lu.setId(1L);
        when(linkUserFactory.createLinkUser(username, created)).thenReturn(lu);
        when(linkUserRepository.save(lu)).thenReturn(lu);

        Link result = linkService.createUserLink(username, req);

        verify(linkFactory).createUserLink(req);
        verify(linkRepository).save(created);
        verify(linkUserFactory).createLinkUser(username, created);
        verify(linkUserRepository).save(lu);
        assertThat(result).isSameAs(created);
    }

    @Test
    void updateUserLink_whenNotFound_returnsEmpty() {
        String linkId = UUID.randomUUID().toString();

        UpdateLinkRequestDto req =
                new UpdateLinkRequestDto(
                        "http://hello_world",
                        "", LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().plusDays(2),
                        null
                );

        when(linkRepository.findLinkByShortLinkId(UUID.fromString(linkId))).thenReturn(Optional.empty());

        Optional<Link> result = linkService.updateUserLink(linkId, req);

        assertThat(result).isEmpty();
        verify(linkRepository).findLinkByShortLinkId(UUID.fromString(linkId));
        verify(linkRepository, never()).save(any());
    }

    @Test
    void updateUserLink_whenFound_updatesAndSaves() {
        UUID id = UUID.randomUUID();
        String linkId = id.toString();
        Link existing = new Link();
        existing.setShortLinkId(id);
        existing.setOriginalUrl("old");
        when(linkRepository.findLinkByShortLinkId(id)).thenReturn(Optional.of(existing));
        when(linkRepository.save(any(Link.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateLinkRequestDto req =
                new UpdateLinkRequestDto(
                        "http://hello_world",
                        "new", LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().plusDays(2),
                        null
                );

        Optional<Link> result = linkService.updateUserLink(linkId, req);

        assertThat(result).isPresent();
        Link saved = result.get();
        assertThat(saved.getOriginalUrl()).isEqualTo(req.originalUrl());
        assertThat(saved.getDescription()).isEqualTo(req.description());
        assertThat(saved.getActiveFrom()).isEqualTo(req.activeFrom());
        assertThat(saved.getActiveUntil()).isEqualTo(req.activeUntil());
        verify(linkRepository).save(existing);
    }

    @Test
    void deleteUserLink_invokesRepositoryDeletes() {
        UUID id = UUID.randomUUID();
        String linkId = id.toString();

        // no return values; just verify calls
        linkService.deleteUserLink(linkId);

        verify(linkUserRepository).deleteLinkUserEntityByShortLinkId(id);
        verify(linkRepository).deleteAllByShortLinkId(id);
    }

    @Test
    void getUserLinks_delegatesToRepository() {
        String username = "bob";
        List<Link> expected = List.of(new Link(), new Link());
        when(linkRepository.findUserLinks(username)).thenReturn(expected);

        List<Link> result = linkService.getUserLinks(username);

        assertThat(result).isSameAs(expected);
        verify(linkRepository).findUserLinks(username);
    }

    @Test
    void getOriginalUrl_whenNotFound_returnsEmpty() {
        String uuid = UUID.randomUUID().toString();
        when(linkRepository.findLinkByShortLinkId(UUID.fromString(uuid))).thenReturn(Optional.empty());

        Optional<String> result = linkService.getOriginalUrl(uuid);

        assertThat(result).isEmpty();
    }

    @Test
    void getOriginalUrl_whenBeforeActiveFrom_returnsEmpty() {
        UUID id = UUID.randomUUID();
        String uuid = id.toString();
        Link link = new Link();
        link.setShortLinkId(id);
        link.setActiveFrom(LocalDateTime.now().plusHours(1));
        link.setActiveUntil(LocalDateTime.now().plusDays(1));
        link.setOriginalUrl("https://ok");
        when(linkRepository.findLinkByShortLinkId(id)).thenReturn(Optional.of(link));

        Optional<String> result = linkService.getOriginalUrl(uuid);

        assertThat(result).isEmpty();
    }

    @Test
    void getOriginalUrl_whenAfterActiveUntil_returnsEmpty() {
        UUID id = UUID.randomUUID();
        String uuid = id.toString();
        Link link = new Link();
        link.setShortLinkId(id);
        link.setActiveFrom(LocalDateTime.now().minusDays(2));
        link.setActiveUntil(LocalDateTime.now().minusHours(1));
        link.setOriginalUrl("https://wikipedia");
        when(linkRepository.findLinkByShortLinkId(id)).thenReturn(Optional.of(link));

        Optional<String> result = linkService.getOriginalUrl(uuid);

        assertThat(result).isEmpty();
    }

    @Test
    void getOriginalUrl_whenWithinActiveWindow_returnsOriginalUrl() {
        UUID id = UUID.randomUUID();
        String uuid = id.toString();
        Link link = new Link();
        link.setShortLinkId(id);
        link.setActiveFrom(LocalDateTime.now().minusHours(1));
        link.setActiveUntil(LocalDateTime.now().plusHours(1));
        link.setOriginalUrl("https://x");
        when(linkRepository.findLinkByShortLinkId(id)).thenReturn(Optional.of(link));

        Optional<String> result = linkService.getOriginalUrl(uuid);

        assertThat(result.get()).isEqualTo("https://x");
    }
}
