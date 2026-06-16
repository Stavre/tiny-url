package com.stavre.tinyurl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AnonymousLinkServiceTest {

    @Mock
    private LinkFactory linkFactory;

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private AnonymousLinkService anonymousLinkService;

    @Test
    void getOriginalUrlReturnsMappedUrlWhenLinkIsFound() {
        Link link = new Link();
        link.setOriginalUrl("https://example.com");
        when(linkRepository.findActiveLinkByShortLinkId("abc")).thenReturn(Optional.of(link));
        Optional<String> result = anonymousLinkService.getOriginalUrl("abc");
        assertThat(result).contains("https://example.com");
    }

    @Test
    void getOriginalUrlReturnsEmptyWhenLinkIsNotFound() {
        when(linkRepository.findActiveLinkByShortLinkId("missing")).thenReturn(Optional.empty());
        Optional<String> result = anonymousLinkService.getOriginalUrl("missing");
        assertThat(result).isEmpty();
    }

    @Test
    void createAnonymousLinkSavesAndReturnsLink() {
        Link link = new Link();
        link.setOriginalUrl("https://example.com");
        when(linkFactory.createAnonymousLink("https://example.com")).thenReturn(link);
        when(linkRepository.save(link)).thenReturn(link);
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        Link result = anonymousLinkService.createAnonymousLink(dto);
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void saveWithRetryRetriesOnCollisionAndSucceeds() {
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        Link link = new Link();
        when(linkFactory.createAnonymousLink("https://example.com")).thenReturn(link);
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("dup"))
                .thenReturn(link);
        Link result = anonymousLinkService.createAnonymousLink(dto);
        assertThat(result).isNotNull();
        verify(linkRepository, times(2)).save(any(Link.class));
    }

    @Test
    void saveWithRetryThrowsIllegalStateAfterFiveFailures() {
        Link link = new Link();
        when(linkFactory.createAnonymousLink("https://example.com")).thenReturn(link);
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("dup"));
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        assertThatThrownBy(() -> anonymousLinkService.createAnonymousLink(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("5 attempts");
        verify(linkRepository, times(5)).save(any(Link.class));
    }
}
