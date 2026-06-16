package com.stavre.tinyurl.controller.auth;

import com.stavre.tinyurl.dto.LinkCountDto;
import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AuthLinkService;
import com.stavre.tinyurl.service.LinkStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final LinkStatisticsService linkStatisticsService;
    private final AuthLinkService authLinkService;

    @GetMapping("/dashboard")
    public String getDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();

        LinkCountDto linkCount = linkStatisticsService.getLinkCount(username);
        List<Link> userLinks = authLinkService.getUserLinks(username);

        model.addAttribute("linkCount", linkCount);
        model.addAttribute("links", userLinks);

        return "auth-users/dashboard.html";
    }
}
