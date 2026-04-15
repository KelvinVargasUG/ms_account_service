package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.UpdateMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class UpdateMovementUseCaseImpl implements UpdateMovementUseCase {

    private final MovementRepositoryPort movementRepository;

    @Override
    public Movement execute(UUID id, TipoMovimiento tipoMovimiento, BigDecimal valor) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(
                        "Movimiento no encontrado con id: " + id));
        if (tipoMovimiento != null) {
            movement.setTipoMovimiento(tipoMovimiento);
        }
        if (valor != null) {
            movement.setValor(valor);
        }
        return movementRepository.save(movement);
    }
}
