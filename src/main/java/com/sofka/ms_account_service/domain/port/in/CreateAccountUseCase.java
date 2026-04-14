package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateAccountUseCase {
    Account execute(TipoCuenta tipoCuenta, BigDecimal saldoInicial, UUID clienteId);
}
