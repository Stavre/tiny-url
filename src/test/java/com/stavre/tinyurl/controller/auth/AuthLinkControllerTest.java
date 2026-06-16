package com.stavre.tinyurl.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class AuthLinkControllerTest {

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
    void getUpdateLinkPageReturnsEditView() throws Exception {
        Link link = buildLink("abc123");
        when(authLinkService.getLinkForEdit("abc123")).thenReturn(Optional.of(link));
        mockMvc.perform(get("/update-link/abc123").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/edit-link.html"))
                .andExpect(model().attributeExists("link"));
    }

    @Test
    void getUpdateLinkPageReturnsNotFoundViewWhenLinkMissing() throws Exception {
        when(authLinkService.getLinkForEdit("missing")).thenReturn(Optional.empty());
        mockMvc.perform(get("/update-link/missing").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("no-link-found.html"));
    }

    @Test
    void postUpdateLinkReturnsDisplayPageOnSuccess() throws Exception {
        Link link = buildLink("abc123");
        when(authLinkService.updateUserLink(eq("abc123"), any())).thenReturn(Optional.of(link));
        mockMvc.perform(post("/update-link")
                        .with(csrf())
                        .with(user("john").roles("USER"))
                        .param("shortLinkId", "abc123")
                        .param("originalUrl", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/display-short-link-page.html"));
    }

    @Test
    void postUpdateLinkReturnsEditViewWithErrorOnInvalidUrl() throws Exception {
        Link link = buildLink("abc123");
        when(authLinkService.getLinkForEdit("abc123")).thenReturn(Optional.of(link));
        mockMvc.perform(post("/update-link")
                        .with(csrf())
                        .with(user("john").roles("USER"))
                        .param("shortLinkId", "abc123")
                        .param("originalUrl", "not-a-url"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/edit-link.html"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postDeleteLinkRedirectsToDashboard() throws Exception {
        mockMvc.perform(post("/delete-link/abc123")
                        .with(csrf())
                        .with(user("john").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
        verify(authLinkService).deleteUserLink("abc123");
    }

    private Link buildLink(String shortLinkId) {
        Link link = new Link();
        link.setShortLinkId(shortLinkId);
        link.setOriginalUrl("https://example.com");
        return link;
    }
}
