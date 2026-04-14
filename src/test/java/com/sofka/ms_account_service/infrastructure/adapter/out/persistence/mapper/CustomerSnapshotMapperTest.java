package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.CustomerSnapshotEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerSnapshotMapperTest {

    CustomerSnapshotMapper mapper;

    final UUID clienteId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @BeforeEach
    void setUp() {
        mapper = new CustomerSnapshotMapper();
    }

    @Test
    void shouldMapEntityToDomain() {
        CustomerSnapshotEntity entity = new CustomerSnapshotEntity(clienteId, "Juan Pérez", true);

        CustomerSnapshot domain = mapper.toDomain(entity);

        assertThat(domain.getClienteId()).isEqualTo(clienteId);
        assertThat(domain.getNombre()).isEqualTo("Juan Pérez");
        assertThat(domain.getActivo()).isTrue();
    }

    @Test
    void shouldMapDomainToEntity() {
        CustomerSnapshot domain = new CustomerSnapshot(clienteId, "María López", false);

        CustomerSnapshotEntity entity = mapper.toEntity(domain);

        assertThat(entity.getClienteId()).isEqualTo(clienteId);
        assertThat(entity.getNombre()).isEqualTo("María López");
        assertThat(entity.getActivo()).isFalse();
    }
}
