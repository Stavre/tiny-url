package com.stavre.tinyurl.controller.authenticateduser;

import com.stavre.tinyurl.service.authenticateduser.AuthenticatedUserLinkService;
import com.stavre.tinyurl.service.authenticateduser.LinkAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/redirect")
public class UserRedirectController {

    private final AuthenticatedUserLinkService authenticatedUserLinkService;
    private final LinkAccessService linkAccessService;

    @GetMapping("/user/{shortUrl}")
    public String redirectUserLink(@PathVariable String shortUrl) {
        Optional<String> originalUrl = authenticatedUserLinkService.getOriginalUrl(shortUrl);

        if (originalUrl.isEmpty()) {
            return "redirect:/redirect/no-link-found";
        }

        linkAccessService.recordAccess(shortUrl);

        return "redirect:" + originalUrl.get();

    }

    @GetMapping("/no-link-found")
    public String noLinkFound() {
        return "no-link-found.html";
    }
}
