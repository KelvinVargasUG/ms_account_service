package com.sofka.ms_account_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Account {

    private static final long MAX_ACCOUNT_NUMBER = 10_000_000_000L;

    private UUID id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldo;
    private Boolean estado;
    private UUID clienteId;

    public Account() {}

    public Account(UUID id, String numeroCuenta, TipoCuenta tipoCuenta,
                   BigDecimal saldoInicial, BigDecimal saldo, Boolean estado, UUID clienteId) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldoInicial = saldoInicial;
        this.saldo = saldo;
        this.estado = estado;
        this.clienteId = clienteId;
    }

    public static Account create(TipoCuenta tipoCuenta,
                                  BigDecimal saldoInicial, UUID clienteId) {
        String numeroCuenta = String.format("%010d",
                ThreadLocalRandom.current().nextLong(0L, MAX_ACCOUNT_NUMBER));
        return new Account(null, numeroCuenta, tipoCuenta, saldoInicial, saldoInicial, true, clienteId);
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

    public UUID getId() {
        return id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
