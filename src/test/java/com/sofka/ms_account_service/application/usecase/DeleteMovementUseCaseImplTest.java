package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteMovementUseCaseImplTest {

    @Mock
    private MovementRepositoryPort movementRepository;

    @Mock
    private AccountRepositoryPort accountRepository;

    private DeleteMovementUseCaseImpl useCase;

    private final UUID movementId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID clienteId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DeleteMovementUseCaseImpl(movementRepository, accountRepository);
    }

    @Test
    void shouldCreateReversalRetiroWhenDeletingDeposit() {
        Movement deposit = Movement.builder()
                .id(movementId)
                .fecha(LocalDateTime.now())
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.valueOf(500))
                .saldo(BigDecimal.valueOf(1500))
                .cuentaId(accountId)
                .build();

        Account account = Account.builder()
                .id(accountId)
                .numeroCuenta("1234567890")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.valueOf(1000))
                .saldo(BigDecimal.valueOf(1500))
                .estado(true)
                .clienteId(clienteId)
                .build();

        when(movementRepository.findById(movementId)).thenReturn(Optional.of(deposit));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));

        Movement result = useCase.execute(movementId);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(accountCaptor.getValue().getSaldo()));

        ArgumentCaptor<Movement> movementCaptor = ArgumentCaptor.forClass(Movement.class);
        verify(movementRepository).save(movementCaptor.capture());
        Movement reversal = movementCaptor.getValue();
        assertEquals(TipoMovimiento.RETIRO, reversal.getTipoMovimiento());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(reversal.getValor()));

        assertNotNull(result);
        assertEquals(TipoMovimiento.RETIRO, result.getTipoMovimiento());
        verify(movementRepository).deleteById(movementId);
    }

    @Test
    void shouldCreateReversalDepositoWhenDeletingRetiro() {
        Movement withdrawal = Movement.builder()
                .id(movementId)
                .fecha(LocalDateTime.now())
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(BigDecimal.valueOf(300))
                .saldo(BigDecimal.valueOf(700))
                .cuentaId(accountId)
                .build();

        Account account = Account.builder()
                .id(accountId)
                .numeroCuenta("1234567890")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.valueOf(1000))
                .saldo(BigDecimal.valueOf(700))
                .estado(true)
                .clienteId(clienteId)
                .build();

        when(movementRepository.findById(movementId)).thenReturn(Optional.of(withdrawal));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));

        Movement result = useCase.execute(movementId);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(accountCaptor.getValue().getSaldo()));

        ArgumentCaptor<Movement> movementCaptor = ArgumentCaptor.forClass(Movement.class);
        verify(movementRepository).save(movementCaptor.capture());
        Movement reversal = movementCaptor.getValue();
        assertEquals(TipoMovimiento.DEPOSITO, reversal.getTipoMovimiento());
        assertEquals(0, BigDecimal.valueOf(300).compareTo(reversal.getValor()));

        assertNotNull(result);
        assertEquals(TipoMovimiento.DEPOSITO, result.getTipoMovimiento());
        verify(movementRepository).deleteById(movementId);
    }

    @Test
    void shouldThrowWhenMovementNotFound() {
        when(movementRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(MovementNotFoundException.class, () -> useCase.execute(movementId));
        verify(accountRepository, never()).save(any(Account.class));
        verify(movementRepository, never()).deleteById(movementId);
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        Movement movement = Movement.builder()
                .id(movementId)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.valueOf(200))
                .cuentaId(accountId)
                .build();

        when(movementRepository.findById(movementId)).thenReturn(Optional.of(movement));
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(movementId));
        verify(movementRepository, never()).deleteById(movementId);
    }
}
