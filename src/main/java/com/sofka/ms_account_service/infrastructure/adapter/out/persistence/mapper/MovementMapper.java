package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.MovementJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovementMapper {

    public Movement toDomain(MovementJpaEntity entity) {
        return new Movement(
                entity.getId(),
                entity.getFecha(),
                entity.getTipoMovimiento(),
                entity.getValor(),
                entity.getSaldo(),
                entity.getCuentaId()
        );
    }

    public MovementJpaEntity toEntity(Movement movement) {
        MovementJpaEntity entity = new MovementJpaEntity();
        entity.setId(movement.getId());
        entity.setFecha(movement.getFecha());
        entity.setTipoMovimiento(movement.getTipoMovimiento());
        entity.setValor(movement.getValor());
        entity.setSaldo(movement.getSaldo());
        entity.setCuentaId(movement.getCuentaId());
        return entity;
    }
}
