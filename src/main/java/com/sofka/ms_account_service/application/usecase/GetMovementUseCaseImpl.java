package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.port.in.GetMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GetMovementUseCaseImpl implements GetMovementUseCase {

    private final MovementRepositoryPort movementRepository;

    @Override
    public Movement findById(UUID id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(
                        "Movimiento no encontrado con id: " + id));
    }

    @Override
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }
}
