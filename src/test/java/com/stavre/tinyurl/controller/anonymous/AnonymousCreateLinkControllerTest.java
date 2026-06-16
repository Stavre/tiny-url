package com.stavre.tinyurl.controller.anonymous;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@SpringBootTest
@AutoConfigureMockMvc
class AnonymousCreateLinkControllerTest {

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
    void getCreateLinkPageReturnsAnonymousTemplateForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/create-link").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("anonymous-users/create-link.html"));
    }

    @Test
    void getCreateLinkPageReturnsAuthTemplateForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/create-link").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/create-link.html"));
    }

    @Test
    void postCreateLinkReturnsDisplayPageOnSuccess() throws Exception {
        Link link = new Link();
        link.setShortLinkId("abc123");
        link.setOriginalUrl("https://example.com");
        when(anonymousLinkService.createAnonymousLink(any())).thenReturn(link);
        mockMvc.perform(post("/create-link")
                        .with(csrf())
                        .param("url", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("anonymous-users/display-short-link-page.html"));
    }

    @Test
    void postCreateLinkReturnsFormWithErrorOnInvalidUrl() throws Exception {
        mockMvc.perform(post("/create-link")
                        .with(csrf())
                        .param("url", "not-a-valid-url"))
                .andExpect(status().isOk())
                .andExpect(view().name("anonymous-users/create-link.html"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postCreateLinkReturnsFormWithErrorOnBlankUrl() throws Exception {
        mockMvc.perform(post("/create-link")
                        .with(csrf())
                        .param("url", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("anonymous-users/create-link.html"))
                .andExpect(model().attributeExists("error"));
    }
}
