package com.stavre.tinyurl.controller.anonymous;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AnonymousLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AnonymousCreateLinkController {

    private final AnonymousLinkService anonymousLinkService;

    @GetMapping("/create-link")
    public String getCreateLinkPage(Authentication authentication) {
        if (authentication == null) {
            return "anonymous-users/create-link.html";
        }
        return "auth-users/create-link.html";
    }

    @PostMapping("/create-link")
    public String createLink(@Valid CreateLinkRequestDto createLinkRequestDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            model.addAttribute("url", createLinkRequestDto.url());
            model.addAttribute("description", createLinkRequestDto.description());
            return "anonymous-users/create-link.html";
        }

        Link link = anonymousLinkService.createAnonymousLink(createLinkRequestDto);
        model.addAttribute("link", link);

        return "anonymous-users/display-short-link-page.html";
    }
}
