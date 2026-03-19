package com.stavre.tinyurl.factory;

import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.entity.LinkUser;
import org.springframework.stereotype.Component;

@Component
public class LinkUserFactory {
    public LinkUser createLinkUser(String username, Link link) {
        LinkUser linkUser = new LinkUser();
        linkUser.setUserName(username);
        linkUser.setShortLinkId(link.getShortLinkId());

        return linkUser;
    }
}
