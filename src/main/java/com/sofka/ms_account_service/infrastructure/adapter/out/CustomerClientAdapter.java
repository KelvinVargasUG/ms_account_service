package com.sofka.ms_account_service.infrastructure.adapter.out;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.CustomerSnapshotRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerClientAdapter implements CustomerClientPort {

    private final CustomerSnapshotRepositoryPort snapshotRepository;

    @Override
    public boolean isClientActive(UUID clienteId) {
        return snapshotRepository.findByClienteId(clienteId)
                .map(CustomerSnapshot::getActivo)
                .orElse(false);
    }

    @Override
    public String getClienteName(UUID clienteId) {
        return snapshotRepository.findByClienteId(clienteId)
                .map(CustomerSnapshot::getNombre)
                .orElse(clienteId.toString());
    }
}
