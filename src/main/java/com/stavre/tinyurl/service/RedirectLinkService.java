package com.stavre.tinyurl.service;

import com.stavre.tinyurl.entity.Link;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedirectLinkService {

    @Value("${app.redirect.anonymous}")
    private String baseAnonymous;

    @Value("${app.redirect.authenticated}")
    private String baseAuthenticated;

    public String getRedirectLink(Link linkEntity) {
        return baseAuthenticated + linkEntity.getShortLinkId().toString();
    }
}
