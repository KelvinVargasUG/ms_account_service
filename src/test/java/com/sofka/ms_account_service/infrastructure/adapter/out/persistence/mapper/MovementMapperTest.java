package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.MovementJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MovementMapperTest {

    MovementMapper mapper;

    final UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    final UUID cuentaId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    final LocalDateTime fecha = LocalDateTime.of(2024, 1, 15, 10, 30);

    @BeforeEach
    void setUp() {
        mapper = new MovementMapper();
    }

    @Test
    void shouldMapEntityToDomain() {
        MovementJpaEntity entity = new MovementJpaEntity();
        entity.setId(id);
        entity.setFecha(fecha);
        entity.setTipoMovimiento(TipoMovimiento.DEPOSITO);
        entity.setValor(BigDecimal.valueOf(200));
        entity.setSaldo(BigDecimal.valueOf(700));
        entity.setCuentaId(cuentaId);

        Movement domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getFecha()).isEqualTo(fecha);
        assertThat(domain.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(domain.getValor()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(domain.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(700));
        assertThat(domain.getCuentaId()).isEqualTo(cuentaId);
    }

    @Test
    void shouldMapDomainToEntity() {
        Movement domain = new Movement(id, fecha, TipoMovimiento.RETIRO,
                BigDecimal.valueOf(-100), BigDecimal.valueOf(400), cuentaId);

        MovementJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFecha()).isEqualTo(fecha);
        assertThat(entity.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(entity.getValor()).isEqualByComparingTo(BigDecimal.valueOf(-100));
        assertThat(entity.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(entity.getCuentaId()).isEqualTo(cuentaId);
    }
}
