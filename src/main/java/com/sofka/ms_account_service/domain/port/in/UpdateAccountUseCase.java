package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;

import java.util.UUID;

public interface UpdateAccountUseCase {
    Account execute(UUID id, TipoCuenta tipoCuenta, Boolean estado);
}
