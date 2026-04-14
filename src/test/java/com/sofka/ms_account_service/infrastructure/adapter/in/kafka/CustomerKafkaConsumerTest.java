package com.sofka.ms_account_service.infrastructure.adapter.in.kafka;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerSnapshotRepositoryPort;
import com.sofka.ms_account_service.infrastructure.adapter.in.kafka.dto.CustomerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerKafkaConsumerTest {

    @Mock
    CustomerSnapshotRepositoryPort snapshotRepository;

    @Mock
    AccountRepositoryPort accountRepository;

    CustomerKafkaConsumer consumer;

    final UUID clienteId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    final UUID accountId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    Account account;

    @BeforeEach
    void setUp() {
        consumer = new CustomerKafkaConsumer(snapshotRepository, accountRepository);
        account = new Account(accountId, "9876543210", TipoCuenta.AHORRO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), true, clienteId);
    }

    @Test
    void shouldDiscardNullEvent() {
        consumer.consume(null);

        verify(snapshotRepository, never()).save(any());
        verify(snapshotRepository, never()).deleteByClienteId(any());
        verify(accountRepository, never()).saveAll(any());
    }

    @Test
    void shouldDiscardEventWithNullClienteId() {
        CustomerEvent event = new CustomerEvent(null, "Test", true, CustomerEvent.EventType.CREATED);

        consumer.consume(event);

        verify(snapshotRepository, never()).save(any());
        verify(accountRepository, never()).saveAll(any());
    }

    @Test
    void shouldUpsertSnapshotOnCreatedEvent() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of());
        CustomerEvent event = new CustomerEvent(clienteId, "Luis Herrera", true, CustomerEvent.EventType.CREATED);

        consumer.consume(event);

        ArgumentCaptor<CustomerSnapshot> captor = ArgumentCaptor.forClass(CustomerSnapshot.class);
        verify(snapshotRepository).save(captor.capture());
        assertThat(captor.getValue().getClienteId()).isEqualTo(clienteId);
        assertThat(captor.getValue().getNombre()).isEqualTo("Luis Herrera");
        assertThat(captor.getValue().getActivo()).isTrue();
    }

    @Test
    void shouldUpsertSnapshotOnUpdatedEvent() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of());
        CustomerEvent event = new CustomerEvent(clienteId, "Luis Herrera", false, CustomerEvent.EventType.UPDATED);

        consumer.consume(event);

        ArgumentCaptor<CustomerSnapshot> captor = ArgumentCaptor.forClass(CustomerSnapshot.class);
        verify(snapshotRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }

    @Test
    void shouldDeleteSnapshotOnDeletedEvent() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of());
        CustomerEvent event = new CustomerEvent(clienteId, null, null, CustomerEvent.EventType.DELETED);

        consumer.consume(event);

        verify(snapshotRepository).deleteByClienteId(clienteId);
        verify(snapshotRepository, never()).save(any());
    }

    @Test
    void shouldActivateAccountsWhenClientCreatedWithActivo() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of(account));
        when(accountRepository.saveAll(any())).thenReturn(List.of(account));
        CustomerEvent event = new CustomerEvent(clienteId, "Luis Herrera", true, CustomerEvent.EventType.CREATED);

        consumer.consume(event);

        ArgumentCaptor<List<Account>> captor = ArgumentCaptor.forClass(List.class);
        verify(accountRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getEstado()).isTrue();
    }

    @Test
    void shouldDeactivateAccountsWhenClientUpdatedWithInactivo() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of(account));
        when(accountRepository.saveAll(any())).thenReturn(List.of(account));
        CustomerEvent event = new CustomerEvent(clienteId, "Luis Herrera", false, CustomerEvent.EventType.UPDATED);

        consumer.consume(event);

        ArgumentCaptor<List<Account>> captor = ArgumentCaptor.forClass(List.class);
        verify(accountRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getEstado()).isFalse();
    }

    @Test
    void shouldDeactivateAccountsWhenClientDeleted() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of(account));
        when(accountRepository.saveAll(any())).thenReturn(List.of(account));
        CustomerEvent event = new CustomerEvent(clienteId, null, null, CustomerEvent.EventType.DELETED);

        consumer.consume(event);

        ArgumentCaptor<List<Account>> captor = ArgumentCaptor.forClass(List.class);
        verify(accountRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getEstado()).isFalse();
    }

    @Test
    void shouldSkipSaveAllWhenNoAccountsFound() {
        when(accountRepository.findByClienteId(clienteId)).thenReturn(List.of());
        CustomerEvent event = new CustomerEvent(clienteId, null, null, CustomerEvent.EventType.DELETED);

        consumer.consume(event);

        verify(accountRepository, never()).saveAll(any());
    }
}
