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

    public Movement() {
    }

    public Movement(UUID id, LocalDateTime fecha, TipoMovimiento tipoMovimiento,
                    BigDecimal valor, BigDecimal saldo, UUID cuentaId) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuentaId = cuentaId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public UUID getCuentaId() {
        return cuentaId;
    }

    public static Movement create(BigDecimal valor, BigDecimal saldoResultante, UUID cuentaId) {
        TipoMovimiento tipo = valor.compareTo(BigDecimal.ZERO) >= 0
                ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO;
        return builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipo)
                .valor(valor)
                .saldo(saldoResultante)
                .cuentaId(cuentaId)
                .build();
    }

    public static Movement create(TipoMovimiento tipoMovimiento, BigDecimal valor,
                                   BigDecimal saldoResultante, UUID cuentaId) {
        return builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(valor)
                .saldo(saldoResultante)
                .cuentaId(cuentaId)
                .build();
    }

    public static MovementBuilder builder() {
        return new MovementBuilder();
    }

    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static class MovementBuilder {

        private UUID id;
        private LocalDateTime fecha;
        private TipoMovimiento tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldo;
        private UUID cuentaId;

        public MovementBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public MovementBuilder fecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public MovementBuilder tipoMovimiento(TipoMovimiento tipoMovimiento) {
            this.tipoMovimiento = tipoMovimiento;
            return this;
        }

        public MovementBuilder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public MovementBuilder saldo(BigDecimal saldo) {
            this.saldo = saldo;
            return this;
        }

        public MovementBuilder cuentaId(UUID cuentaId) {
            this.cuentaId = cuentaId;
            return this;
        }

        public Movement build() {
            return new Movement(id, fecha, tipoMovimiento, valor, saldo, cuentaId);
        }
    }

}
