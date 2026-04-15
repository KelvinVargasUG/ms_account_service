package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Movement;

import java.util.List;
import java.util.UUID;

public interface GetMovementUseCase {
    Movement findById(UUID id);
    List<Movement> findAll();
}
