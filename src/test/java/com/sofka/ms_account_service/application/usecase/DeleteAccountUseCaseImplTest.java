package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseImplTest {

    @Mock
    AccountRepositoryPort accountRepository;

    DeleteAccountUseCaseImpl useCase;

    final UUID accountId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    final UUID clienteId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    @BeforeEach
    void setUp() {
        useCase = new DeleteAccountUseCaseImpl(accountRepository);
    }

    @Test
    void shouldDeactivateAndSaveAccount() {
        Account active = new Account(accountId, "1111111111", TipoCuenta.AHORRO,
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), true, clienteId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(active));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(accountId);

        assertThat(active.getEstado()).isFalse();
        verify(accountRepository).save(active);
    }

    @Test
    void shouldThrowWhenAccountAlreadyDeleted() {
        Account inactive = new Account(accountId, "1111111111", TipoCuenta.AHORRO,
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), false, clienteId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.execute(accountId))
                .isInstanceOf(InactiveAccountException.class)
                .hasMessageContaining(accountId.toString());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(accountRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(unknownId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
