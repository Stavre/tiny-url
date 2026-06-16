package com.stavre.tinyurl.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class LinkTest {

    @Test
    void isActiveReturnsTrueWhenBothDatesAreNull() {
        Link link = new Link();
        assertThat(link.isActive()).isTrue();
    }

    @Test
    void isActiveReturnsTrueWhenWithinActiveWindow() {
        Link link = new Link();
        link.setActiveFrom(LocalDateTime.now().minusDays(1));
        link.setActiveUntil(LocalDateTime.now().plusDays(1));
        assertThat(link.isActive()).isTrue();
    }

    @Test
    void isActiveReturnsFalseWhenExpired() {
        Link link = new Link();
        link.setActiveFrom(LocalDateTime.now().minusDays(2));
        link.setActiveUntil(LocalDateTime.now().minusSeconds(1));
        assertThat(link.isActive()).isFalse();
    }

    @Test
    void isActiveReturnsFalseWhenPending() {
        Link link = new Link();
        link.setActiveFrom(LocalDateTime.now().plusSeconds(1));
        link.setActiveUntil(LocalDateTime.now().plusDays(2));
        assertThat(link.isActive()).isFalse();
    }

    @Test
    void isActiveReturnsTrueWhenActiveFromIsNullAndActiveUntilIsFuture() {
        Link link = new Link();
        link.setActiveUntil(LocalDateTime.now().plusDays(1));
        assertThat(link.isActive()).isTrue();
    }

    @Test
    void isActiveReturnsTrueWhenActiveFromIsPastAndActiveUntilIsNull() {
        Link link = new Link();
        link.setActiveFrom(LocalDateTime.now().minusDays(1));
        assertThat(link.isActive()).isTrue();
    }
}
