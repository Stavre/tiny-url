package com.stavre.tinyurl.controller;

import com.stavre.tinyurl.service.LinkAccessService;
import com.stavre.tinyurl.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class RedirectController {

    private final LinkService authenticatedUserLinkService;
    private final LinkAccessService linkAccessService;

    @GetMapping("/redirect/{shortUrl}")
    public String redirectUserLink(@PathVariable String shortUrl) {
        Optional<String> originalUrl = authenticatedUserLinkService.getOriginalUrl(shortUrl);

        if (originalUrl.isEmpty()) {
            return "redirect:/no-link-found";
        }

        linkAccessService.recordAccess(shortUrl);

        return "redirect:" + originalUrl.get();

    }

    @GetMapping("/no-link-found")
    public String noLinkFound() {
        return "no-link-found.html";
    }
}
