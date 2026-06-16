package com.stavre.tinyurl.controller.auth;

import com.stavre.tinyurl.dto.CreateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AuthLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthCreateLinkController {

    private final AuthLinkService authLinkService;

    @PostMapping("/create-link/auth")
    public String createLink(@Valid CreateLinkRequestDto createLinkRequestDto,
                             BindingResult bindingResult,
                             Model model,
                             Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("url", createLinkRequestDto.url());
            model.addAttribute("description", createLinkRequestDto.description());
            return "auth-users/create-link.html";
        }

        Link link = authLinkService.createUserLink(authentication.getName(), createLinkRequestDto);
        model.addAttribute("link", link);

        return "auth-users/display-short-link-page.html";
    }
}
