package com.stavre.tinyurl.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class LinkFactoryTest {

    private final LinkFactory factory = new LinkFactory();

    @Test
    void createAnonymousLinkSetsOriginalUrl() {
        Link link = factory.createAnonymousLink("https://example.com");
        assertThat(link.getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void createAnonymousLinkGeneratesShortLinkId() {
        Link link = factory.createAnonymousLink("https://example.com");
        assertThat(link.getShortLinkId()).hasSize(6);
    }

    @Test
    void createAnonymousLinkSetsActiveUntilThreeDaysFromNow() {
        LocalDateTime lowerBound = LocalDateTime.now().plusDays(3).minusSeconds(1);
        Link link = factory.createAnonymousLink("https://example.com");
        LocalDateTime upperBound = LocalDateTime.now().plusDays(3).plusSeconds(1);
        assertThat(link.getActiveUntil()).isBetween(lowerBound, upperBound);
    }

    @Test
    void createUserLinkSetsUrlAndDescription() {
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", "my desc", null, null);
        Link link = factory.createUserLink(dto);
        assertThat(link.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(link.getDescription()).isEqualTo("my desc");
    }

    @Test
    void createUserLinkDefaultsActiveUntilToFiveDaysWhenNullProvided() {
        LocalDateTime lowerBound = LocalDateTime.now().plusDays(5).minusSeconds(1);
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        Link link = factory.createUserLink(dto);
        LocalDateTime upperBound = LocalDateTime.now().plusDays(5).plusSeconds(1);
        assertThat(link.getActiveUntil()).isBetween(lowerBound, upperBound);
    }

    @Test
    void createUserLinkUsesProvidedActiveDates() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime until = LocalDateTime.now().plusDays(10);
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, from, until);
        Link link = factory.createUserLink(dto);
        assertThat(link.getActiveFrom()).isEqualTo(from);
        assertThat(link.getActiveUntil()).isEqualTo(until);
    }

    @Test
    void createUserLinkSetsUpdatedAtToNull() {
        CreateLinkRequestDto dto = new CreateLinkRequestDto("https://example.com", null, null, null);
        Link link = factory.createUserLink(dto);
        assertThat(link.getUpdatedAt()).isNull();
    }
}
