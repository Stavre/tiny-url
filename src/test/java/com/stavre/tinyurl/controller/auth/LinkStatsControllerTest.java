package com.stavre.tinyurl.controller.auth;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.stavre.tinyurl.entity.Link;
import com.stavre.tinyurl.service.AnonymousLinkService;
import com.stavre.tinyurl.service.AuthLinkService;
import com.stavre.tinyurl.service.LinkStatisticsService;
import com.stavre.tinyurl.service.LinkUsageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class LinkStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnonymousLinkService anonymousLinkService;

    @MockitoBean
    private AuthLinkService authLinkService;

    @MockitoBean
    private LinkUsageService linkUsageService;

    @MockitoBean
    private LinkStatisticsService linkStatisticsService;

    @Test
    void getLinkStatsReturnsStatsViewWithModel() throws Exception {
        Link link = buildLink("abc123");
        List<LocalDateTime> timestamps = List.of(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1)
        );
        when(authLinkService.getLinkForEdit("abc123")).thenReturn(Optional.of(link));
        when(linkUsageService.getUsageTimestamps("abc123")).thenReturn(timestamps);
        mockMvc.perform(get("/link-stats/abc123").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/link-stats.html"))
                .andExpect(model().attributeExists("link", "timestamps", "totalUses"));
    }

    @Test
    void getLinkStatsReturnsNotFoundViewWhenLinkMissing() throws Exception {
        when(authLinkService.getLinkForEdit("missing")).thenReturn(Optional.empty());
        mockMvc.perform(get("/link-stats/missing").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("no-link-found.html"));
    }

    @Test
    void getLinkStatsHandlesEmptyUsageList() throws Exception {
        Link link = buildLink("abc123");
        when(authLinkService.getLinkForEdit("abc123")).thenReturn(Optional.of(link));
        when(linkUsageService.getUsageTimestamps("abc123")).thenReturn(List.of());
        mockMvc.perform(get("/link-stats/abc123").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/link-stats.html"))
                .andExpect(model().attribute("totalUses", 0));
    }

    @Test
    void getLinkStatsRedirectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/link-stats/abc123"))
                .andExpect(status().is3xxRedirection());
    }

    private Link buildLink(String shortLinkId) {
        Link link = new Link();
        link.setShortLinkId(shortLinkId);
        link.setOriginalUrl("https://example.com");
        return link;
    }
}
