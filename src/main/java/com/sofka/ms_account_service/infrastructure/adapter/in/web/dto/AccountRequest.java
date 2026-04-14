package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoCuenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(
        @NotNull TipoCuenta tipoCuenta,
        @NotNull @PositiveOrZero BigDecimal saldoInicial,
        @NotNull UUID clienteId
) {}
