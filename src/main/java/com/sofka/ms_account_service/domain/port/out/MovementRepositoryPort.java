package com.sofka.ms_account_service.domain.port.out;

import com.sofka.ms_account_service.domain.model.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    Optional<Movement> findById(UUID id);
    List<Movement> findAll();
    void deleteById(UUID id);
    List<Movement> findByAccountIdsAndDateRange(List<UUID> accountIds, LocalDateTime start, LocalDateTime end);
}
