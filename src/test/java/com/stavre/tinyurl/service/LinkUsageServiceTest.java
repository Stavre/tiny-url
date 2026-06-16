package com.stavre.tinyurl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stavre.tinyurl.entity.LinkUsage;
import com.stavre.tinyurl.repository.LinkUsageRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LinkUsageServiceTest {

    @Mock
    private LinkUsageRepository linkUsageRepository;

    @Mock
    private LinkUserRepository linkUserRepository;

    @InjectMocks
    private LinkUsageService linkUsageService;

    @Test
    void logUsageSavesRecordWhenLinkIsTracked() {
        when(linkUserRepository.existsByShortLinkId("abc")).thenReturn(true);
        linkUsageService.logUsage("abc");
        ArgumentCaptor<LinkUsage> captor = ArgumentCaptor.forClass(LinkUsage.class);
        verify(linkUsageRepository).save(captor.capture());
        assertThat(captor.getValue().getShortLinkId()).isEqualTo("abc");
        assertThat(captor.getValue().getUsedAt()).isNotNull();
    }

    @Test
    void logUsageSkipsSaveWhenLinkIsNotTracked() {
        when(linkUserRepository.existsByShortLinkId("anon")).thenReturn(false);
        linkUsageService.logUsage("anon");
        verify(linkUsageRepository, never()).save(any(LinkUsage.class));
    }

    @Test
    void getUsageTimestampsReturnsTruncatedTimestamps() {
        LinkUsage usage = new LinkUsage();
        usage.setShortLinkId("abc");
        usage.setUsedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 45, 999_000_000));
        when(linkUsageRepository.findByShortLinkIdOrderByUsedAtAsc("abc"))
                .thenReturn(List.of(usage));
        List<LocalDateTime> result = linkUsageService.getUsageTimestamps("abc");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNano()).isZero();
        assertThat(result.get(0).getSecond()).isEqualTo(45);
    }

    @Test
    void getUsageTimestampsReturnsEmptyListWhenNoUsagesExist() {
        when(linkUsageRepository.findByShortLinkIdOrderByUsedAtAsc("abc"))
                .thenReturn(List.of());
        List<LocalDateTime> result = linkUsageService.getUsageTimestamps("abc");
        assertThat(result).isEmpty();
    }
}
