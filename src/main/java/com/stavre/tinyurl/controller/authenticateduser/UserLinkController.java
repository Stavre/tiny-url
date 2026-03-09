package com.stavre.tinyurl.controller.authenticateduser;

import com.stavre.tinyurl.dto.authenticateduser.CreateLinkRequestDto;
import com.stavre.tinyurl.dto.authenticateduser.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import com.stavre.tinyurl.repository.authenticateduser.AuthenticatedUserLinkRepository;
import com.stavre.tinyurl.service.RedirectLinkService;
import com.stavre.tinyurl.service.authenticateduser.AuthenticatedUserLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserLinkController {

    private final AuthenticatedUserLinkService linkService;
    private final RedirectLinkService redirectLinkService;
    private final AuthenticatedUserLinkRepository linkRepository;

    @GetMapping("/create-link")
    public String getCreateLinkPage() {
        return "auth-users/create-link.html";
    }

    @PostMapping("/create-link")
    public String createLink(CreateLinkRequestDto createLinkRequestDto, Model model) {

        AuthenticatedUserLinkEntity link = linkService.createUserLink(createLinkRequestDto);

        model.addAttribute("link", link);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/display-short-link-page.html";
    }

    @GetMapping("/edit-link/{linkId}")
    public String getEditLinkPage(@PathVariable(name = "linkId") String linkId, Model model) {
        Optional<AuthenticatedUserLinkEntity> link = linkRepository.findLinkByShortLinkId(UUID.fromString(linkId));

        if (link.isEmpty()) {
            return "no-link-found.html";
        }

        model.addAttribute("link", link.get());
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/edit-link.html";
    }

    @PostMapping("/update-link")
    public String updateLink(UpdateLinkRequestDto requestDto, Model model) {
        Optional<AuthenticatedUserLinkEntity> updatedLink = linkService.updateUserLink(requestDto);

        if (updatedLink.isEmpty()) {
            return "no-link-found.html";
        }

        AuthenticatedUserLinkEntity linkEntity = updatedLink.get();

        model.addAttribute("link", linkEntity);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/display-short-link-page.html";
    }

    @Transactional
    @PostMapping("/delete-link/{linkId}")
    public String deleteLink(@PathVariable String linkId) {
        linkService.deleteLink(linkId);
        return "redirect:/user/dashboard";
    }
}
