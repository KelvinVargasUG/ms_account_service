package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.model.ReportEntry;
import com.sofka.ms_account_service.domain.port.in.GenerateReportUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    GenerateReportUseCase generateReportUseCase;

    MockMvc mockMvc;

    final String numeroCuenta = "1234567890";

    @BeforeEach
    void setUp() {
        ReportController controller = new ReportController(generateReportUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn200WithReportEntries() throws Exception {
        ReportEntry entry = new ReportEntry(
                "Juan Pérez", "1234567890", "AHORRO",
                BigDecimal.valueOf(500), true,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                "DEPOSITO", BigDecimal.valueOf(200), BigDecimal.valueOf(700));

        when(generateReportUseCase.execute(eq(numeroCuenta), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(entry));

        mockMvc.perform(get("/reportes")
                        .param("numeroCuenta", numeroCuenta)
                        .param("fechaInicio", "2024-01-01")
                        .param("fechaFin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].cliente").value("Juan Pérez"))
                .andExpect(jsonPath("$.data[0].tipoMovimiento").value("DEPOSITO"));
    }

    @Test
    void shouldReturn200WithEmptyList() throws Exception {
        when(generateReportUseCase.execute(eq(numeroCuenta), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/reportes")
                        .param("numeroCuenta", numeroCuenta)
                        .param("fechaInicio", "2024-01-01")
                        .param("fechaFin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldReturn500OnUnexpectedException() throws Exception {
        when(generateReportUseCase.execute(eq(numeroCuenta), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/reportes")
                        .param("numeroCuenta", numeroCuenta)
                        .param("fechaInicio", "2024-01-01")
                        .param("fechaFin", "2024-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500));
    }

    @Test
    void shouldReturn200WithNoParams() throws Exception {
        when(generateReportUseCase.execute(isNull(), isNull(), isNull()))
                .thenReturn(List.of());

        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
