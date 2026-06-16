package com.stavre.tinyurl.controller.auth;

import com.stavre.tinyurl.dto.UpdateLinkRequestDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AuthLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthLinkController {

    private final AuthLinkService authLinkService;

    @GetMapping("/update-link/{linkId}")
    public String getUpdateLinkPage(@PathVariable(name = "linkId") String linkId, Model model) {
        Optional<Link> link = authLinkService.getLinkForEdit(linkId);

        if (link.isEmpty()) {
            return "no-link-found.html";
        }

        model.addAttribute("link", link.get());

        return "auth-users/edit-link.html";
    }

    @PostMapping("/update-link")
    public String updateLink(String shortLinkId,
                             @Valid UpdateLinkRequestDto requestDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            Optional<Link> link = authLinkService.getLinkForEdit(shortLinkId);
            if (link.isEmpty()) {
                return "no-link-found.html";
            }
            model.addAttribute("link", link.get());
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "auth-users/edit-link.html";
        }

        Optional<Link> updatedLink = authLinkService.updateUserLink(shortLinkId, requestDto);

        if (updatedLink.isEmpty()) {
            return "no-link-found.html";
        }

        model.addAttribute("link", updatedLink.get());

        return "auth-users/display-short-link-page.html";
    }

    @PostMapping("/delete-link/{linkId}")
    public String deleteLink(@PathVariable String linkId) {
        authLinkService.deleteUserLink(linkId);
        return "redirect:/dashboard";
    }
}
