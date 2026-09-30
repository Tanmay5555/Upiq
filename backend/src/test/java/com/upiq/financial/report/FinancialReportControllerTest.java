package com.upiq.financial.report;

import com.upiq.auth.model.Role;
import com.upiq.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialReportController.class)
@ContextConfiguration(classes = {FinancialReportController.class,
        FinancialReportControllerTest.TestSecurityConfiguration.class})
class FinancialReportControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private FinancialReportService reportService;

    @TestConfiguration
    static class TestSecurityConfiguration {
        @Bean
        SecurityFilterChain reportTestSecurity(HttpSecurity http) throws Exception {
            return http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .build();
        }
    }

    @Test
    void authenticatedPrincipalGetsPdfAndItsUserIdScopesTheReport() throws Exception {
        User user = User.builder().id(41L).email("a@example.test").password("x")
                .role(Role.USER).createdAt(LocalDateTime.now()).build();
        when(reportService.generate(41L)).thenReturn(new GeneratedFinancialReport(
                "upiq-financial-report-2026-08.pdf", "%PDF-1.4".getBytes()));
        var auth = new UsernamePasswordAuthenticationToken(user, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(get("/api/financial/report").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"upiq-financial-report-2026-08.pdf\""));
        verify(reportService).generate(41L);
        verify(reportService, never()).generate(42L);
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mvc.perform(get("/api/financial/report"))
                .andExpect(status().is4xxClientError());
        verifyNoInteractions(reportService);
    }
}
