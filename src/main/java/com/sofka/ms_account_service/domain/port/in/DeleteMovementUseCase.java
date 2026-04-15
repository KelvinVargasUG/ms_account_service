package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Movement;

import java.util.UUID;

public interface DeleteMovementUseCase {
    Movement execute(UUID id);
}
