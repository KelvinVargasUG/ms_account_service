package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.CustomerSnapshotEntity;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.CustomerSnapshotMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.CustomerSnapshotJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSnapshotRepositoryAdapterTest {

    @Mock
    CustomerSnapshotJpaRepository jpaRepository;

    @Mock
    CustomerSnapshotMapper mapper;

    CustomerSnapshotRepositoryAdapter adapter;

    final UUID clienteId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    CustomerSnapshotEntity entity;
    CustomerSnapshot domain;

    @BeforeEach
    void setUp() {
        adapter = new CustomerSnapshotRepositoryAdapter(jpaRepository, mapper);
        entity = new CustomerSnapshotEntity(clienteId, "Carlos Ruiz", true);
        domain = new CustomerSnapshot(clienteId, "Carlos Ruiz", true);
    }

    @Test
    void shouldFindByClienteId() {
        when(jpaRepository.findById(clienteId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<CustomerSnapshot> result = adapter.findByClienteId(clienteId);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Carlos Ruiz");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(clienteId)).thenReturn(Optional.empty());

        Optional<CustomerSnapshot> result = adapter.findByClienteId(clienteId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveSnapshot() {
        when(mapper.toEntity(domain)).thenReturn(entity);

        adapter.save(domain);

        verify(jpaRepository).save(entity);
    }

    @Test
    void shouldDeleteByClienteId() {
        adapter.deleteByClienteId(clienteId);

        verify(jpaRepository).deleteById(clienteId);
    }
}
