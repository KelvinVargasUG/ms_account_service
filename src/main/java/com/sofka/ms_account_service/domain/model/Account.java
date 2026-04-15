package com.sofka.ms_account_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private static final long MAX_ACCOUNT_NUMBER = 10_000_000_000L;

    @Setter
    private UUID id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldo;
    private Boolean estado;
    private UUID clienteId;

    public static Account create(TipoCuenta tipoCuenta,
                                  BigDecimal saldoInicial, UUID clienteId) {
        String numeroCuenta = String.format("%010d",
                ThreadLocalRandom.current().nextLong(0L, MAX_ACCOUNT_NUMBER));
        return builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta(tipoCuenta)
                .saldoInicial(saldoInicial)
                .saldo(saldoInicial)
                .estado(true)
                .clienteId(clienteId)
                .build();
    }

    public void updateTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public void updateEstado(Boolean estado) {
        this.estado = estado;
    }

    public void deactivate() {
        this.estado = false;
    }

    public void applyMovement(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

}
