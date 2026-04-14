package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
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
class UpdateAccountUseCaseImplTest {

    @Mock
    AccountRepositoryPort accountRepository;

    UpdateAccountUseCaseImpl useCase;

    final UUID accountId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    final UUID clienteId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    Account sample;

    @BeforeEach
    void setUp() {
        useCase = new UpdateAccountUseCaseImpl(accountRepository);
        sample = new Account(accountId, "9876543210", TipoCuenta.AHORRO,
                BigDecimal.valueOf(200), BigDecimal.valueOf(200), true, clienteId);
    }

    @Test
    void shouldUpdateTipoCuentaAndEstado() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = useCase.execute(accountId, TipoCuenta.CORRIENTE, false);

        assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(result.getEstado()).isFalse();
        verify(accountRepository).save(any());
    }

    @Test
    void shouldUpdateOnlyTipoCuentaWhenEstadoIsNull() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = useCase.execute(accountId, TipoCuenta.CORRIENTE, null);

        assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(result.getEstado()).isTrue();
    }

    @Test
    void shouldUpdateOnlyEstadoWhenTipoCuentaIsNull() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = useCase.execute(accountId, null, false);

        assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        assertThat(result.getEstado()).isFalse();
    }

    @Test
    void shouldNotCheckDuplicateWhenTypeUnchanged() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = useCase.execute(accountId, TipoCuenta.AHORRO, null);

        assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        verify(accountRepository, never()).findByClienteId(any());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(accountRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(unknownId, TipoCuenta.CORRIENTE, true))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
