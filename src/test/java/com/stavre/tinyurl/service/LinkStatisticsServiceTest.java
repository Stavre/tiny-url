package com.stavre.tinyurl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stavre.tinyurl.dto.LinkCountDto;
import com.stavre.tinyurl.repository.LinkUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkStatisticsServiceTest {

    @Mock
    private LinkUserRepository linkUserRepository;

    @InjectMocks
    private LinkStatisticsService linkStatisticsService;

    @Test
    void getLinkCountReturnsDtoWithCorrectCounts() {
        when(linkUserRepository.countByUserNameIs("john")).thenReturn(10L);
        when(linkUserRepository.countActiveLinksByUserName("john")).thenReturn(7L);
        when(linkUserRepository.countExpiredLinksByUserName("john")).thenReturn(3L);
        LinkCountDto result = linkStatisticsService.getLinkCount("john");
        assertThat(result.totalLinks()).isEqualTo(10L);
        assertThat(result.activeLinks()).isEqualTo(7L);
        assertThat(result.expiredLinks()).isEqualTo(3L);
    }

    @Test
    void getLinkCountReturnsZerosForNewUser() {
        when(linkUserRepository.countByUserNameIs("newuser")).thenReturn(0L);
        when(linkUserRepository.countActiveLinksByUserName("newuser")).thenReturn(0L);
        when(linkUserRepository.countExpiredLinksByUserName("newuser")).thenReturn(0L);
        LinkCountDto result = linkStatisticsService.getLinkCount("newuser");
        assertThat(result.totalLinks()).isZero();
        assertThat(result.activeLinks()).isZero();
        assertThat(result.expiredLinks()).isZero();
    }
}
