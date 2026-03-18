package com.stavre.tinyurl.controller;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.repository.LinkRepository;
import com.stavre.tinyurl.service.LinkService;
import com.stavre.tinyurl.service.RedirectLinkService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;
    private final RedirectLinkService redirectLinkService;
    private final LinkRepository linkRepository;

    @GetMapping("/create-link")
    public String getCreateLinkPage(Authentication authentication) {
        boolean isAnonymousUser = authentication == null;
        if (isAnonymousUser) {
            return "anonymous-users/create-link.html";

        }
        return "auth-users/create-link.html";
    }

    @PostMapping("/create-link")
    public String createLink(CreateLinkRequestDto createLinkRequestDto, Model model, Authentication authentication) {
        Link link;
        String page;

        boolean isAnonymousUser = authentication == null;
        if (isAnonymousUser) {
            link = linkService.createAnonymousLink(createLinkRequestDto);
            page = "anonymous-users/display-short-link-page.html";
        } else {
            link = linkService.createUserLink(authentication.getName(), createLinkRequestDto);
            page = "auth-users/display-short-link-page.html";
        }

        model.addAttribute("link", link);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return page;
    }

    @GetMapping("/update-link/{linkId}")
    public String getUpdateLinkPage(@PathVariable(name = "linkId") String linkId, Model model) {
        Optional<Link> link = linkRepository.findLinkByShortLinkId(UUID.fromString(linkId));

        if (link.isEmpty()) {
            return "no-link-found.html";
        }

        model.addAttribute("link", link.get());
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/edit-link.html";
    }

    @PostMapping("/update-link")
    public String updateLink(UpdateLinkRequestDto requestDto, Model model) {
        Optional<Link> updatedLink = linkService.updateUserLink(requestDto);

        if (updatedLink.isEmpty()) {
            return "no-link-found.html";
        }

        Link linkEntity = updatedLink.get();

        model.addAttribute("link", linkEntity);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/display-short-link-page.html";
    }

    @Transactional
    @PostMapping("/delete-link/{linkId}")
    public String deleteLink(@PathVariable String linkId) {
        linkService.deleteLink(linkId);
        return "redirect:/dashboard";
    }
}
