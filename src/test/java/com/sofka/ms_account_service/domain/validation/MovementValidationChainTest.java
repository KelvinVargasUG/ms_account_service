package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementValidationChainTest {

    @Mock
    AccountRepositoryPort accountRepository;

    @Mock
    CustomerClientPort customerClient;

    MovementValidationHandler chain;

    final String numeroCuenta = "1234567890";
    final UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID clienteId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    final BigDecimal saldo = BigDecimal.valueOf(1000);

    @BeforeEach
    void setUp() {
        AccountExistsHandler accountExists = new AccountExistsHandler(accountRepository);
        ClientActiveHandler clientActive = new ClientActiveHandler(customerClient);
        AccountActiveHandler accountActive = new AccountActiveHandler();
        SufficientBalanceHandler sufficientBalance = new SufficientBalanceHandler();

        accountExists.setNext(clientActive);
        clientActive.setNext(accountActive);
        accountActive.setNext(sufficientBalance);

        chain = accountExists;
    }

    @Test
    void shouldPassAllValidationsForValidDeposit() {
        Account account = new Account(
                accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldo, saldo, true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.of(account));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        MovementValidationContext context =
                new MovementValidationContext(numeroCuenta, TipoMovimiento.DEPOSITO, saldo);
        chain.handle(context);

        assertThat(context.getAccount()).isEqualTo(account);
    }

    @Test
    void shouldFailWhenAccountNotFound() {
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.empty());

        MovementValidationContext context = new MovementValidationContext(
                numeroCuenta, TipoMovimiento.DEPOSITO, BigDecimal.valueOf(100));

        assertThatThrownBy(() -> chain.handle(context))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(numeroCuenta);
    }

    @Test
    void shouldFailWhenClientInactive() {
        Account account = new Account(
                accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldo, saldo, true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.of(account));
        when(customerClient.isClientActive(clienteId)).thenReturn(false);

        MovementValidationContext context = new MovementValidationContext(
                numeroCuenta, TipoMovimiento.DEPOSITO, BigDecimal.valueOf(100));

        assertThatThrownBy(() -> chain.handle(context))
                .isInstanceOf(InvalidClientException.class)
                .hasMessageContaining(clienteId.toString());
    }

    @Test
    void shouldFailWhenAccountInactive() {
        Account account = new Account(
                accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldo, saldo, false, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.of(account));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        MovementValidationContext context = new MovementValidationContext(
                numeroCuenta, TipoMovimiento.DEPOSITO, BigDecimal.valueOf(100));

        assertThatThrownBy(() -> chain.handle(context))
                .isInstanceOf(InactiveAccountException.class)
                .hasMessageContaining(numeroCuenta);
    }

    @Test
    void shouldFailWhenInsufficientBalance() {
        Account account = new Account(
                accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldo, saldo, true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.of(account));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        MovementValidationContext context = new MovementValidationContext(
                numeroCuenta, TipoMovimiento.RETIRO, BigDecimal.valueOf(2000));

        assertThatThrownBy(() -> chain.handle(context))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo no disponible");
    }

    @Test
    void shouldPassWhenWithdrawalEqualsBalance() {
        Account account = new Account(
                accountId, numeroCuenta, TipoCuenta.AHORRO,
                saldo, saldo, true, clienteId);
        when(accountRepository.findByNumeroCuenta(numeroCuenta))
                .thenReturn(Optional.of(account));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        MovementValidationContext context =
                new MovementValidationContext(numeroCuenta, TipoMovimiento.RETIRO, saldo);
        chain.handle(context);

        assertThat(context.getAccount()).isEqualTo(account);
    }
}
