package com.sofka.ms_account_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReportEntry(
        String cliente,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado,
        LocalDateTime fecha,
        String tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldoDisponible
) {}
