package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.ReportEntry;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateReportUseCaseImplTest {

    @Mock AccountRepositoryPort accountRepository;
    @Mock MovementRepositoryPort movementRepository;
    @Mock CustomerClientPort customerClient;

    GenerateReportUseCaseImpl useCase;

    final String numeroCuenta = "1234567890";
    final UUID clienteId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        useCase = new GenerateReportUseCaseImpl(accountRepository, movementRepository, customerClient);
    }

    @Test
    void shouldGenerateReportWithMovements() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        Account acc = new Account(accountId, "1234567890", TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), true, clienteId);
        Movement mov = new Movement(UUID.randomUUID(), LocalDateTime.of(2024, 1, 15, 10, 0),
                TipoMovimiento.DEPOSITO, BigDecimal.valueOf(500), BigDecimal.valueOf(1500), accountId);

        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(java.util.Optional.of(acc));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of(acc));
        when(customerClient.getClienteName(clienteId)).thenReturn("Juan Perez");
        when(movementRepository.findByAccountIdsAndDateRange(eq(List.of(accountId)), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(mov));

        List<ReportEntry> report = useCase.execute(numeroCuenta, start, end);

        assertThat(report).hasSize(1);
        assertThat(report.get(0).cliente()).isEqualTo("Juan Perez");
        assertThat(report.get(0).numeroCuenta()).isEqualTo("1234567890");
        assertThat(report.get(0).valor()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void shouldGenerateReportWithNullDates() {
        Account acc = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), true, clienteId);
        Movement mov = new Movement(UUID.randomUUID(), LocalDateTime.now(),
                TipoMovimiento.DEPOSITO, BigDecimal.valueOf(200), BigDecimal.valueOf(1200), accountId);

        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(java.util.Optional.of(acc));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of(acc));
        when(customerClient.getClienteName(clienteId)).thenReturn("Juan Perez");
        when(movementRepository.findByAccountIdsAndDateRange(eq(List.of(accountId)), any(), any()))
                .thenReturn(List.of(mov));

        List<ReportEntry> report = useCase.execute(numeroCuenta, null, null);

        assertThat(report).hasSize(1);
    }

    @Test
    void shouldGenerateReportWithNullNumeroCuenta() {
        Account acc = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), true, clienteId);
        Movement mov = new Movement(UUID.randomUUID(), LocalDateTime.now(),
                TipoMovimiento.DEPOSITO, BigDecimal.valueOf(200), BigDecimal.valueOf(1200), accountId);

        when(accountRepository.findAll()).thenReturn(List.of(acc));
        when(customerClient.getClienteName(clienteId)).thenReturn("Juan Perez");
        when(movementRepository.findByAccountIdsAndDateRange(eq(List.of(accountId)), any(), any()))
                .thenReturn(List.of(mov));

        List<ReportEntry> report = useCase.execute(null, null, null);

        assertThat(report).hasSize(1);
        verify(accountRepository, never()).findByNumeroCuenta(any());
    }

    @Test
    void shouldReturnEmptyReportWhenNoMovements() {
        Account acc = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(java.util.Optional.of(acc));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of());
        when(movementRepository.findByAccountIdsAndDateRange(anyList(), any(), any()))
                .thenReturn(List.of());

        List<ReportEntry> report = useCase.execute(numeroCuenta,
                LocalDate.now().minusDays(30), LocalDate.now());

        assertThat(report).isEmpty();
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> useCase.execute(numeroCuenta,
                LocalDate.now().minusDays(30), LocalDate.now()))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(numeroCuenta);

        verify(customerClient, never()).isClientActive(any());
    }

    @Test
    void shouldThrowWhenClientInactive() {
        Account acc = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(java.util.Optional.of(acc));
        when(customerClient.isClientActive(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(numeroCuenta,
                LocalDate.now().minusDays(30), LocalDate.now()))
                .isInstanceOf(com.sofka.ms_account_service.domain.exception.InvalidClientException.class)
                .hasMessageContaining(clienteId.toString());

        verify(accountRepository, never()).findByClienteId(any());
    }
}
