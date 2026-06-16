package com.stavre.tinyurl.controller.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.stavre.tinyurl.dto.LinkCountDto;
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
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

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
    void getDashboardReturnsDashboardView() throws Exception {
        when(linkStatisticsService.getLinkCount(anyString()))
                .thenReturn(new LinkCountDto(0L, 0L, 0L));
        when(authLinkService.getUserLinks(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/dashboard").with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth-users/dashboard.html"))
                .andExpect(model().attributeExists("linkCount", "links"));
    }

    @Test
    void getDashboardRedirectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}
