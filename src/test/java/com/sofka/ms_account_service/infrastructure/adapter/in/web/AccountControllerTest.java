package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.in.CreateAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.DeleteAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.GetAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.UpdateAccountUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.AccountRequest;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock CreateAccountUseCase createAccountUseCase;
    @Mock GetAccountUseCase getAccountUseCase;
    @Mock UpdateAccountUseCase updateAccountUseCase;
    @Mock DeleteAccountUseCase deleteAccountUseCase;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    final UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID clienteId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    Account sampleAccount;

    @BeforeEach
    void setUp() {
        sampleAccount = new Account(accountId, "1234567890", TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
        AccountController controller = new AccountController(
                createAccountUseCase, getAccountUseCase, updateAccountUseCase, deleteAccountUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201WhenCreatingAccount() throws Exception {
        when(createAccountUseCase.execute(any(), any(BigDecimal.class), any(UUID.class)))
                .thenReturn(sampleAccount);

        AccountRequest req = new AccountRequest(TipoCuenta.AHORRO, BigDecimal.valueOf(1000), clienteId);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldReturn200WhenGettingAllAccounts() throws Exception {
        when(getAccountUseCase.findAll()).thenReturn(List.of(sampleAccount));

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void shouldReturn200WhenGettingAccountById() throws Exception {
        when(getAccountUseCase.findById(accountId)).thenReturn(sampleAccount);

        mockMvc.perform(get("/cuentas/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numeroCuenta").value("1234567890"));
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        UUID unknownId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        when(getAccountUseCase.findById(unknownId))
                .thenThrow(new AccountNotFoundException("Cuenta no encontrada con id: " + unknownId));

        mockMvc.perform(get("/cuentas/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldReturn200WhenDeletingAccount() throws Exception {
        doNothing().when(deleteAccountUseCase).execute(accountId);

        mockMvc.perform(delete("/cuentas/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cuenta eliminada correctamente"));
    }

    @Test
    void shouldIgnoreBalanceFieldInUpdateRequest() throws Exception {
        Account updatedAccount = new Account(accountId, "1234567890", TipoCuenta.CORRIENTE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
        when(updateAccountUseCase.execute(eq(accountId), eq(TipoCuenta.CORRIENTE), eq(true)))
                .thenReturn(updatedAccount);

        String body = "{\"tipoCuenta\":\"CORRIENTE\",\"estado\":true,\"saldo\":99999}";

        mockMvc.perform(put("/cuentas/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.saldo").value(1000));

        verify(updateAccountUseCase).execute(accountId, TipoCuenta.CORRIENTE, true);
    }
}
