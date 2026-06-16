package com.stavre.tinyurl.service;

import com.stavre.tinyurl.entity.LinkUsage;
import com.stavre.tinyurl.repository.LinkUsageRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkUsageService {

    private final LinkUsageRepository linkUsageRepository;
    private final LinkUserRepository linkUserRepository;

    public void logUsage(String shortLinkId) {
        if (!linkUserRepository.existsByShortLinkId(shortLinkId)) {
            return;
        }
        LinkUsage usage = new LinkUsage();
        usage.setShortLinkId(shortLinkId);
        usage.setUsedAt(LocalDateTime.now());
        linkUsageRepository.save(usage);
    }

    public List<LocalDateTime> getUsageTimestamps(String shortLinkId) {
        return linkUsageRepository.findByShortLinkIdOrderByUsedAtAsc(shortLinkId)
                .stream()
                .map(u -> u.getUsedAt().truncatedTo(ChronoUnit.SECONDS))
                .toList();
    }
}
