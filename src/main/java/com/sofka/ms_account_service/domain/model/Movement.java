package com.sofka.ms_account_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Movement {

    private UUID id;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
    private UUID cuentaId;

    public Movement() {}

    public Movement(UUID id, LocalDateTime fecha, TipoMovimiento tipoMovimiento,
                    BigDecimal valor, BigDecimal saldo, UUID cuentaId) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuentaId = cuentaId;
    }

    public static Movement create(BigDecimal valor, BigDecimal saldoResultante, UUID cuentaId) {
        TipoMovimiento tipo = valor.compareTo(BigDecimal.ZERO) >= 0
                ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO;
        return new Movement(null, LocalDateTime.now(), tipo, valor, saldoResultante, cuentaId);
    }

    public static Movement create(TipoMovimiento tipoMovimiento, BigDecimal valor,
                                   BigDecimal saldoResultante, UUID cuentaId) {
        return new Movement(null, LocalDateTime.now(), tipoMovimiento, valor, saldoResultante, cuentaId);
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public UUID getCuentaId() {
        return cuentaId;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
