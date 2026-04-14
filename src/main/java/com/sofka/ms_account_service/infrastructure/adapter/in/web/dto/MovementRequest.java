package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MovementRequest(
        @NotBlank String numeroCuenta,
        @NotNull TipoCuenta tipoCuenta,
        @NotNull Boolean estado,
        @NotNull TipoMovimiento tipoMovimiento,
        @NotNull BigDecimal valor
) {}
