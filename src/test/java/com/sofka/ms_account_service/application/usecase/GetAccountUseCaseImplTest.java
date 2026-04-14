package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountUseCaseImplTest {

    @Mock
    AccountRepositoryPort accountRepository;
    @Mock
    CustomerClientPort customerClient;

    GetAccountUseCaseImpl useCase;

    final UUID accountId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    final UUID clienteId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    Account sample;

    @BeforeEach
    void setUp() {
        useCase = new GetAccountUseCaseImpl(accountRepository, customerClient);
        sample = new Account(accountId, "1234567890", TipoCuenta.AHORRO,
                BigDecimal.valueOf(500), BigDecimal.valueOf(500), true, clienteId);
    }

    @Test
    void shouldReturnAccountWhenFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);

        Account result = useCase.findById(accountId);

        assertThat(result.getId()).isEqualTo(accountId);
        assertThat(result.getNumeroCuenta()).isEqualTo("1234567890");
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(accountRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.findById(unknownId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    @Test
    void shouldThrowWhenClientInactiveOnFindById() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sample));
        when(customerClient.isClientActive(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.findById(accountId))
                .isInstanceOf(InvalidClientException.class)
                .hasMessageContaining(clienteId.toString());
    }

    @Test
    void shouldReturnOnlyActiveClientAccounts() {
        UUID inactiveClientId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Account inactiveClientAccount = new Account(UUID.randomUUID(), "9999999999", TipoCuenta.CORRIENTE,
                BigDecimal.valueOf(200), BigDecimal.valueOf(200), true, inactiveClientId);

        when(accountRepository.findAll()).thenReturn(List.of(sample, inactiveClientAccount));
        when(customerClient.isClientActive(clienteId)).thenReturn(true);
        when(customerClient.isClientActive(inactiveClientId)).thenReturn(false);

        List<Account> result = useCase.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(accountId);
    }

    @Test
    void shouldReturnEmptyListWhenNoAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of());

        List<Account> result = useCase.findAll();

        assertThat(result).isEmpty();
    }
}
