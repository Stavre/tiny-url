package com.stavre.tinyurl.controller.authenticateduser;

import com.stavre.tinyurl.dto.authenticateduser.LinkCountDto;
import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import com.stavre.tinyurl.service.authenticateduser.AuthenticatedUserLinkService;
import com.stavre.tinyurl.service.authenticateduser.LinkStatisticsService;
import com.stavre.tinyurl.service.RedirectLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class DashboardController {

    private final LinkStatisticsService linkStatisticsService;
    private final RedirectLinkService redirectLinkService;
    private final AuthenticatedUserLinkService linkService;

    @GetMapping("/dashboard")
    public String getDashboard(Authentication authentication, Model model) {

        String username = authentication.getName();

        LinkCountDto linkCount = linkStatisticsService.getLinkCount(username);
        List<AuthenticatedUserLinkEntity> userLinks = linkService.getUserLinks(username);

        model.addAttribute("linkCount", linkCount);
        model.addAttribute("links", userLinks);
        model.addAttribute("redirectLinkFormatter", redirectLinkService);

        return "auth-users/dashboard.html";
    }
}
