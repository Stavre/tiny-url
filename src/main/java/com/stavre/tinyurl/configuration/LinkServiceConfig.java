package com.stavre.tinyurl.configuration;

import com.stavre.tinyurl.factory.LinkFactory;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.repository.LinkUserRepository;
import com.stavre.tinyurl.service.AnonymousLinkService;
import com.stavre.tinyurl.service.AuthLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LinkServiceConfig {

    private final LinkFactory linkFactory;
    private final LinkRepository linkRepository;
    private final LinkUserRepository linkUserRepository;

    @Bean
    public AnonymousLinkService anonymousLinkService() {
        return new AnonymousLinkService(linkFactory, linkRepository);
    }

    @Bean
    public AuthLinkService authLinkService() {
        return new AuthLinkService(linkFactory, linkRepository, linkUserRepository);
    }
}
