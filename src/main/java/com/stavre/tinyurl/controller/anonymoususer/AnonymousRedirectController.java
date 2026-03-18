package com.stavre.tinyurl.controller.anonymoususer;

import com.stavre.tinyurl.service.anonymoususer.AnonymousUserLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/redirect")
public class AnonymousRedirectController {

    private final AnonymousUserLinkService anonymousUserLinkService;

    @GetMapping("/anonymous/{shortUrl}")
    public String redirectAnonymousLink(@PathVariable String shortUrl) {
        Optional<String> originalUrl = anonymousUserLinkService.getOriginalUrl(shortUrl);

        return originalUrl.map(s -> "redirect:" + s).orElse("redirect:/redirect/no-link-found");
    }
}
