package com.sofka.ms_account_service.domain.port.out;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;

import java.util.Optional;
import java.util.UUID;

public interface CustomerSnapshotRepositoryPort {
    Optional<CustomerSnapshot> findByClienteId(UUID clienteId);
    void save(CustomerSnapshot snapshot);
    void deleteByClienteId(UUID clienteId);
}
