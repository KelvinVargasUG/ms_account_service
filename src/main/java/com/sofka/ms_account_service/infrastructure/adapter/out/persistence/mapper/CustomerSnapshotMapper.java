package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.CustomerSnapshotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerSnapshotMapper {

    public CustomerSnapshot toDomain(CustomerSnapshotEntity entity) {
        return new CustomerSnapshot(entity.getClienteId(), entity.getNombre(), entity.getActivo());
    }

    public CustomerSnapshotEntity toEntity(CustomerSnapshot snapshot) {
        return new CustomerSnapshotEntity(snapshot.getClienteId(), snapshot.getNombre(), snapshot.getActivo());
    }
}
