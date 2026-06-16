package com.stavre.tinyurl.controller.auth;

import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AuthLinkService;
import com.stavre.tinyurl.service.LinkUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LinkStatsController {

    private final AuthLinkService authLinkService;
    private final LinkUsageService linkUsageService;

    @GetMapping("/link-stats/{shortLinkId}")
    public String getLinkStats(@PathVariable String shortLinkId, Model model) {
        Optional<Link> link = authLinkService.getLinkForEdit(shortLinkId);

        if (link.isEmpty()) {
            return "no-link-found.html";
        }

        List<String> timestamps = linkUsageService.getUsageTimestamps(shortLinkId)
                .stream()
                .map(Object::toString)
                .toList();

        model.addAttribute("link", link.get());
        model.addAttribute("timestamps", timestamps);
        model.addAttribute("totalUses", timestamps.size());

        return "auth-users/link-stats.html";
    }
}
