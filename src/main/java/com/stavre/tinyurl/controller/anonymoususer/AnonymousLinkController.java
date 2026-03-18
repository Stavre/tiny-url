package com.stavre.tinyurl.controller.anonymoususer;

import com.stavre.tinyurl.entity.anonymoususer.AnonymousLinkEntity;
import com.stavre.tinyurl.service.RedirectLinkService;
import com.stavre.tinyurl.service.anonymoususer.AnonymousUserLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/anonymous")
public class AnonymousLinkController {

    private final RedirectLinkService redirectLinkService;
    private final AnonymousUserLinkService linkService;

    @GetMapping("/create-link")
    public String createLinkPage() {
        return "anonymous-users/create-link-page.html";
    }

    @PostMapping("/create-link/add")
    public String addLink(@RequestParam String link, Model model) {

        AnonymousLinkEntity shortLink = linkService.createAnonymousLink(link);

        model.addAttribute("link", shortLink);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "anonymous-users/display-short-link-page.html";
    }
}
