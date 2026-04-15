package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.DeleteMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.GetMovementUseCase;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MovementControllerTest {

    @Mock
    RegisterMovementUseCase registerMovementUseCase;

    @Mock
    GetMovementUseCase getMovementUseCase;

    @Mock
    DeleteMovementUseCase deleteMovementUseCase;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    final String numeroCuenta = "1234567890";
    final UUID cuentaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID movementId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    MovementRequest sampleRequest;

    @BeforeEach
    void setUp() {
        MovementController controller = new MovementController(
                registerMovementUseCase, getMovementUseCase,
                deleteMovementUseCase);
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

    @Test
    void shouldReturn200WhenListingAllMovements() throws Exception {
        Movement movement = new Movement(movementId, LocalDateTime.now(),
                TipoMovimiento.DEPOSITO, BigDecimal.valueOf(200), BigDecimal.valueOf(700), cuentaId);
        when(getMovementUseCase.findAll()).thenReturn(List.of(movement));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tipoMovimiento").value("DEPOSITO"));
    }

    @Test
    void shouldReturn200WhenListingEmptyMovements() throws Exception {
        when(getMovementUseCase.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturn200WhenFindingMovementById() throws Exception {
        Movement movement = new Movement(movementId, LocalDateTime.now(),
                TipoMovimiento.RETIRO, BigDecimal.valueOf(100), BigDecimal.valueOf(400), cuentaId);
        when(getMovementUseCase.findById(movementId)).thenReturn(movement);

        mockMvc.perform(get("/movimientos/{id}", movementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("RETIRO"))
                .andExpect(jsonPath("$.data.valor").value(100));
    }

    @Test
    void shouldReturn404WhenMovementNotFound() throws Exception {
        when(getMovementUseCase.findById(movementId))
                .thenThrow(new MovementNotFoundException("Movimiento no encontrado: " + movementId));

        mockMvc.perform(get("/movimientos/{id}", movementId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldReturn200WhenDeletingMovementWithReversal() throws Exception {
        Movement reversal = new Movement(UUID.randomUUID(), LocalDateTime.now(),
                TipoMovimiento.RETIRO, BigDecimal.valueOf(200), BigDecimal.valueOf(500), cuentaId);
        when(deleteMovementUseCase.execute(movementId)).thenReturn(reversal);

        mockMvc.perform(delete("/movimientos/{id}", movementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("RETIRO"))
                .andExpect(jsonPath("$.data.valor").value(200));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentMovement() throws Exception {
        when(deleteMovementUseCase.execute(movementId))
                .thenThrow(new MovementNotFoundException("Movimiento no encontrado: " + movementId));

        mockMvc.perform(delete("/movimientos/{id}", movementId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }
}
