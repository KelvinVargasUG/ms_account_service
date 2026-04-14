package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovementResponse(
        UUID id,
        LocalDateTime fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        UUID cuentaId
) {}
