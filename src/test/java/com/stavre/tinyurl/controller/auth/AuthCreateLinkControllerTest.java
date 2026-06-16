package com.stavre.tinyurl.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
class AuthCreateLinkControllerTest {

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
    void postCreateLinkAuthReturnsDisplayPageOnSuccess() throws Exception {
        Link link = new Link();
        link.setShortLinkId("abc123");
        link.setOriginalUrl("https://example.com");
        when(authLinkService.createUserLink(eq("john"), any())).thenReturn(link);
        mockMvc.perform(post("/create-link/auth")
                        .with(csrf())
                        .with(user("john").roles("USER"))
                        .param("url", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/display-short-link-page.html"))
                .andExpect(model().attributeExists("link"));
    }

    @Test
    void postCreateLinkAuthReturnsFormWithErrorOnInvalidUrl() throws Exception {
        mockMvc.perform(post("/create-link/auth")
                        .with(csrf())
                        .with(user("john").roles("USER"))
                        .param("url", "not-a-valid-url"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/create-link.html"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postCreateLinkAuthReturnsFormWithErrorOnBlankUrl() throws Exception {
        mockMvc.perform(post("/create-link/auth")
                        .with(csrf())
                        .with(user("john").roles("USER"))
                        .param("url", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/create-link.html"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postCreateLinkAuthRedirectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/create-link/auth").with(csrf())
                        .param("url", "https://example.com"))
                .andExpect(status().is3xxRedirection());
    }
}
