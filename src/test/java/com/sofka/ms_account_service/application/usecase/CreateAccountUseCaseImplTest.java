package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseImplTest {

    @Mock AccountRepositoryPort accountRepository;
    @Mock CustomerClientPort customerClient;

    CreateAccountUseCaseImpl useCase;

    final UUID clienteId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        useCase = new CreateAccountUseCaseImpl(accountRepository, customerClient);
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        Account saved = new Account(accountId, "1234567890", TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
        when(accountRepository.save(any())).thenReturn(saved);

        Account result = useCase.execute(TipoCuenta.AHORRO, BigDecimal.valueOf(1000), clienteId);

        assertThat(result.getNumeroCuenta()).isEqualTo("1234567890");
        assertThat(result.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(result.getEstado()).isTrue();
        verify(accountRepository).save(any());
    }


    @Test
    void shouldThrowWhenClienteIdInvalid() {
        UUID invalidId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        when(customerClient.isClientActive(invalidId)).thenReturn(false);

        assertThatThrownBy(() ->
                useCase.execute(TipoCuenta.CORRIENTE, BigDecimal.valueOf(500), invalidId))
                .isInstanceOf(InvalidClientException.class);
        verify(accountRepository, never()).save(any());
    }
}
