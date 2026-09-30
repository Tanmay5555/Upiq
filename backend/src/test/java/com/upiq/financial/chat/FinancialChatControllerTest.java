package com.upiq.financial.chat;

import com.upiq.auth.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialChatControllerTest {

    @Mock private FinancialChatService service;
    @InjectMocks private FinancialChatController controller;

    @Test
    void scopesChatQuestionToAuthenticatedPrincipalRatherThanRequestData() {
        User authenticatedUser = User.builder().id(42L).email("demo@example.com").password("unused").build();
        FinancialChatRequest request = new FinancialChatRequest("What was my total income?");
        FinancialChatResponse expected = FinancialChatResponse.builder().answer("Verified.").supported(true).build();
        when(service.answer(request.getQuestion(), 42L)).thenReturn(expected);

        var response = controller.ask(request, authenticatedUser);

        assertEquals(expected, response.getBody().getData());
        verify(service).answer(eq(request.getQuestion()), eq(42L));
    }
}
