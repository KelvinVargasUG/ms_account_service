package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementRequest;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MovementControllerTest {

    @Mock
    RegisterMovementUseCase registerMovementUseCase;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    final String numeroCuenta = "1234567890";
    final UUID cuentaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID movementId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    MovementRequest sampleRequest;

    @BeforeEach
    void setUp() {
        MovementController controller = new MovementController(registerMovementUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        sampleRequest = new MovementRequest(numeroCuenta, TipoCuenta.AHORRO,
                true, TipoMovimiento.DEPOSITO, BigDecimal.valueOf(200));
    }

    @Test
    void shouldReturn201WhenRegisteringDeposit() throws Exception {
        Movement movement = new Movement(movementId, LocalDateTime.now(),
                TipoMovimiento.DEPOSITO, BigDecimal.valueOf(200), BigDecimal.valueOf(700), cuentaId);
        when(registerMovementUseCase.execute(any(), any(), any(), any(), any())).thenReturn(movement);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("DEPOSITO"));
    }

    @Test
    void shouldReturn400WhenInsufficientBalance() throws Exception {
        when(registerMovementUseCase.execute(any(), any(), any(), any(), any()))
                .thenThrow(new InsufficientBalanceException());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldReturn400WhenAccountIsInactive() throws Exception {
        when(registerMovementUseCase.execute(any(), any(), any(), any(), any()))
                .thenThrow(new InactiveAccountException("Cuenta inactiva"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}
