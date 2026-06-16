package com.stavre.tinyurl.controller.common;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class RedirectControllerTest {

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
    void redirectsToOriginalUrlWhenLinkIsFound() throws Exception {
        when(anonymousLinkService.getOriginalUrl("abc123"))
                .thenReturn(Optional.of("https://example.com"));
        mockMvc.perform(get("/redirect/abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));
        verify(linkUsageService).logUsage("abc123");
    }

    @Test
    void redirectsToNoLinkFoundWhenLinkIsNotFound() throws Exception {
        when(anonymousLinkService.getOriginalUrl("unknown"))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/redirect/unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/no-link-found"));
    }

    @Test
    void noLinkFoundEndpointReturnsView() throws Exception {
        mockMvc.perform(get("/no-link-found"))
                .andExpect(status().isOk())
                .andExpect(view().name("no-link-found.html"));
    }
}
