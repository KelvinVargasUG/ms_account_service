package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import com.sofka.ms_account_service.domain.validation.AccountActiveHandler;
import com.sofka.ms_account_service.domain.validation.AccountExistsHandler;
import com.sofka.ms_account_service.domain.validation.ClientActiveHandler;
import com.sofka.ms_account_service.domain.validation.SufficientBalanceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterMovementUseCaseImplTest {

    @Mock AccountRepositoryPort accountRepository;
    @Mock MovementRepositoryPort movementRepository;
    @Mock CustomerClientPort customerClient;

    RegisterMovementUseCaseImpl useCase;

    final String numeroCuenta = "1234567890";
    final UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID clienteId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    final BigDecimal saldoInicial = BigDecimal.valueOf(1000);

    Account activeAccount;

    @BeforeEach
    void setUp() {
        AccountExistsHandler accountExists = new AccountExistsHandler(accountRepository);
        ClientActiveHandler clientActive = new ClientActiveHandler(customerClient);
        AccountActiveHandler accountActive = new AccountActiveHandler();
        SufficientBalanceHandler sufficientBalance = new SufficientBalanceHandler();

        accountExists.setNext(clientActive);
        clientActive.setNext(accountActive);
        accountActive.setNext(sufficientBalance);

        useCase = new RegisterMovementUseCaseImpl(
                accountExists, accountRepository, movementRepository);
        activeAccount = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldoInicial, saldoInicial, true, clienteId);
    }

    private Movement runExecute(BigDecimal valor) {
        return useCase.execute(numeroCuenta, TipoCuenta.AHORRO, true, TipoMovimiento.DEPOSITO, valor);
    }

    @Test
    void shouldRegisterDepositOnExistingAccount() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(activeAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.save(any())).thenReturn(activeAccount);
        Movement saved = Movement.create(BigDecimal.valueOf(500), BigDecimal.valueOf(1500), accountId);
        when(movementRepository.save(any())).thenReturn(saved);

        Movement result = runExecute(BigDecimal.valueOf(500));

        assertThat(result.getValor()).isEqualByComparingTo(BigDecimal.valueOf(500));
        verify(accountRepository).save(any());
        verify(movementRepository).save(any());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> runExecute(BigDecimal.valueOf(100)))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(numeroCuenta);
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenInsufficientBalance() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(activeAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(numeroCuenta, TipoCuenta.AHORRO,
                true, TipoMovimiento.RETIRO, BigDecimal.valueOf(2000)))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo no disponible");
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenClientInactive() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(activeAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> runExecute(BigDecimal.valueOf(100)))
                .isInstanceOf(InvalidClientException.class)
                .hasMessageContaining(clienteId.toString());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAccountInactive() {
        Account inactive = new Account(accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldoInicial, saldoInicial, false, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(inactive));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        assertThatThrownBy(() -> runExecute(BigDecimal.valueOf(100)))
                .isInstanceOf(InactiveAccountException.class);
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldRegisterWithdrawalWithinAvailableBalance() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(activeAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.save(any())).thenReturn(activeAccount);
        Movement saved = Movement.create(TipoMovimiento.RETIRO, BigDecimal.valueOf(200),
                BigDecimal.valueOf(800), accountId);
        when(movementRepository.save(any())).thenReturn(saved);

        Movement result = useCase.execute(numeroCuenta, TipoCuenta.AHORRO,
                true, TipoMovimiento.RETIRO, BigDecimal.valueOf(200));

        assertThat(result.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(result.getValor()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(result.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(800));
        verify(accountRepository).save(any());
        verify(movementRepository).save(any());
    }

    @Test
    void shouldRegisterWithdrawalWhenAmountEqualsBalance() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(activeAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(accountRepository.save(any())).thenReturn(activeAccount);
        Movement saved = Movement.create(TipoMovimiento.RETIRO, saldoInicial,
                BigDecimal.ZERO, accountId);
        when(movementRepository.save(any())).thenReturn(saved);

        Movement result = useCase.execute(numeroCuenta, TipoCuenta.AHORRO,
                true, TipoMovimiento.RETIRO, saldoInicial);

        assertThat(result.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(result.getValor()).isEqualByComparingTo(saldoInicial);
        assertThat(result.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any());
        verify(movementRepository).save(any());
    }
}
