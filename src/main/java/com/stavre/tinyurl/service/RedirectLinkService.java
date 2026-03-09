package com.stavre.tinyurl.service;

import com.stavre.tinyurl.entity.anonymoususer.AnonymousLinkEntity;
import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedirectLinkService {

    @Value("${app.redirect.anonymous}")
    private String baseAnonymous;

    @Value("${app.redirect.authenticated}")
    private String baseAuthenticated;

    public String getRedirectLink(AnonymousLinkEntity linkEntity) {
        return baseAnonymous + linkEntity.getId().toString();
    }

    public String getRedirectLink(AuthenticatedUserLinkEntity linkEntity) {
        return baseAuthenticated + linkEntity.getShortLinkId().toString();
    }
}
