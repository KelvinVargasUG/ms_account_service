package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.port.in.DeleteMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DeleteMovementUseCaseImpl implements DeleteMovementUseCase {

    private final MovementRepositoryPort movementRepository;

    @Override
    public void execute(UUID id) {
        if (movementRepository.findById(id).isEmpty()) {
            throw new MovementNotFoundException(
                    "Movimiento no encontrado con id: " + id);
        }
        movementRepository.deleteById(id);
    }
}
