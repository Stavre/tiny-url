package com.stavre.tinyurl.controller.common;

import com.stavre.tinyurl.service.AnonymousLinkService;
import com.stavre.tinyurl.service.LinkUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class RedirectController {

    private final AnonymousLinkService anonymousLinkService;
    private final LinkUsageService linkUsageService;

    @GetMapping("/redirect/{shortUrl}")
    public String redirectUserLink(@PathVariable String shortUrl) {
        Optional<String> originalUrl = anonymousLinkService.getOriginalUrl(shortUrl);

        if (originalUrl.isEmpty()) {
            return "redirect:/no-link-found";
        }

        linkUsageService.logUsage(shortUrl);
        return "redirect:" + originalUrl.get();
    }

    @GetMapping("/no-link-found")
    public String noLinkFound() {
        return "no-link-found.html";
    }
}
