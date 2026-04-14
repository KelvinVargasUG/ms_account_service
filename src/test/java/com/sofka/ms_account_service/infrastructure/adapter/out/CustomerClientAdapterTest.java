package com.sofka.ms_account_service.infrastructure.adapter.out;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.domain.port.out.CustomerSnapshotRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerClientAdapterTest {

    @Mock
    CustomerSnapshotRepositoryPort snapshotRepository;

    CustomerClientAdapter adapter;

    final UUID clienteId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    @BeforeEach
    void setUp() {
        adapter = new CustomerClientAdapter(snapshotRepository);
    }

    @Test
    void shouldReturnTrueWhenClientIsActive() {
        CustomerSnapshot snapshot = new CustomerSnapshot(clienteId, "Ana Gómez", true);
        when(snapshotRepository.findByClienteId(clienteId)).thenReturn(Optional.of(snapshot));

        boolean result = adapter.isClientActive(clienteId);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenClientIsInactive() {
        CustomerSnapshot snapshot = new CustomerSnapshot(clienteId, "Pedro Torres", false);
        when(snapshotRepository.findByClienteId(clienteId)).thenReturn(Optional.of(snapshot));

        boolean result = adapter.isClientActive(clienteId);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenClientNotFound() {
        when(snapshotRepository.findByClienteId(clienteId)).thenReturn(Optional.empty());

        boolean result = adapter.isClientActive(clienteId);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnClienteNameWhenFound() {
        CustomerSnapshot snapshot = new CustomerSnapshot(clienteId, "Lucía Vargas", true);
        when(snapshotRepository.findByClienteId(clienteId)).thenReturn(Optional.of(snapshot));

        String name = adapter.getClienteName(clienteId);

        assertThat(name).isEqualTo("Lucía Vargas");
    }

    @Test
    void shouldReturnClienteIdAsStringWhenNotFound() {
        when(snapshotRepository.findByClienteId(clienteId)).thenReturn(Optional.empty());

        String name = adapter.getClienteName(clienteId);

        assertThat(name).isEqualTo(clienteId.toString());
    }
}
