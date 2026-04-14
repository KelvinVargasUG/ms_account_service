package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository;

import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.MovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MovementJpaRepository extends JpaRepository<MovementJpaEntity, UUID> {

    @Query("SELECT m FROM MovementJpaEntity m WHERE m.cuentaId IN :cuentaIds AND m.fecha BETWEEN :start AND :end")
    List<MovementJpaEntity> findByCuentaIdsAndFechaBetween(
            @Param("cuentaIds") List<UUID> cuentaIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
