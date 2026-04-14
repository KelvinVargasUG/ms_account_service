package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.domain.port.out.CustomerSnapshotRepositoryPort;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.CustomerSnapshotMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.CustomerSnapshotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerSnapshotRepositoryAdapter implements CustomerSnapshotRepositoryPort {

    private final CustomerSnapshotJpaRepository jpaRepository;
    private final CustomerSnapshotMapper mapper;

    @Override
    public Optional<CustomerSnapshot> findByClienteId(UUID clienteId) {
        return jpaRepository.findById(clienteId).map(mapper::toDomain);
    }

    @Override
    public void save(CustomerSnapshot snapshot) {
        jpaRepository.save(mapper.toEntity(snapshot));
    }

    @Override
    public void deleteByClienteId(UUID clienteId) {
        jpaRepository.deleteById(clienteId);
    }
}
