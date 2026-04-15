package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public record MovementUpdateRequest(
        TipoMovimiento tipoMovimiento,
        BigDecimal valor
) {}
