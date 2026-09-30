package com.upiq.financial.dashboard;

import com.upiq.auth.model.User;
import com.upiq.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/financial/dashboard")
@RequiredArgsConstructor
public class FinancialDashboardController {

    private final FinancialDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<FinancialDashboardResponse>> getDashboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getDashboard(user.getId()), "Financial dashboard retrieved successfully"));
    }
}
