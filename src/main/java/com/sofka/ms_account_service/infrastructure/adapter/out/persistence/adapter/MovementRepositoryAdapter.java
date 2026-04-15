package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.MovementMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.MovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepositoryPort {

    private final MovementJpaRepository jpaRepository;
    private final MovementMapper mapper;

    @Override
    public Movement save(Movement movement) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movement)));
    }

    @Override
    public Optional<Movement> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Movement> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Movement> findByAccountIdsAndDateRange(List<UUID> accountIds, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByCuentaIdsAndFechaBetween(accountIds, start, end)
                .stream().map(mapper::toDomain).toList();
    }
}
