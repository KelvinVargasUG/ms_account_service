package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldo,
        Boolean estado,
        UUID clienteId
) {}
