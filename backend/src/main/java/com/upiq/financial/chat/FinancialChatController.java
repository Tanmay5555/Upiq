package com.upiq.financial.chat;

import com.upiq.auth.model.User;
import com.upiq.config.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/financial-chat")
@RequiredArgsConstructor
public class FinancialChatController {

    private final FinancialChatService financialChatService;

    @PostMapping
    public ResponseEntity<ApiResponse<FinancialChatResponse>> ask(
            @Valid @RequestBody FinancialChatRequest request,
            @AuthenticationPrincipal User user) {
        FinancialChatResponse answer = financialChatService.answer(request.getQuestion(), user.getId());
        return ResponseEntity.ok(ApiResponse.success(answer, "Financial chat response generated"));
    }
}
