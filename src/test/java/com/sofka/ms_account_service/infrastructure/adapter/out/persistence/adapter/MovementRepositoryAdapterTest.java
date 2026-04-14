package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.MovementJpaEntity;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.MovementMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.MovementJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementRepositoryAdapterTest {

    @Mock
    MovementJpaRepository jpaRepository;

    @Mock
    MovementMapper mapper;

    MovementRepositoryAdapter adapter;

    final UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    final UUID cuentaId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    final LocalDateTime fecha = LocalDateTime.of(2024, 3, 1, 8, 0);

    MovementJpaEntity entity;
    Movement domain;

    @BeforeEach
    void setUp() {
        adapter = new MovementRepositoryAdapter(jpaRepository, mapper);

        entity = new MovementJpaEntity();
        entity.setId(id);
        entity.setFecha(fecha);
        entity.setTipoMovimiento(TipoMovimiento.DEPOSITO);
        entity.setValor(BigDecimal.valueOf(300));
        entity.setSaldo(BigDecimal.valueOf(800));
        entity.setCuentaId(cuentaId);

        domain = new Movement(id, fecha, TipoMovimiento.DEPOSITO,
                BigDecimal.valueOf(300), BigDecimal.valueOf(800), cuentaId);
    }

    @Test
    void shouldSave() {
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Movement result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getValor()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void shouldFindByAccountIdsAndDateRange() {
        LocalDateTime start = fecha.minusDays(1);
        LocalDateTime end = fecha.plusDays(1);
        List<UUID> ids = List.of(cuentaId);

        when(jpaRepository.findByCuentaIdsAndFechaBetween(ids, start, end))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Movement> result = adapter.findByAccountIdsAndDateRange(ids, start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCuentaId()).isEqualTo(cuentaId);
    }
}
