package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public class MovementValidationContext {

    private final String numeroCuenta;
    private final TipoMovimiento tipoMovimiento;
    private final BigDecimal valor;
    private Account account;

    public MovementValidationContext(String numeroCuenta,
                                     TipoMovimiento tipoMovimiento,
                                     BigDecimal valor) {
        this.numeroCuenta = numeroCuenta;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
