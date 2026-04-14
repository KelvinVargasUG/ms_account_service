package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public interface RegisterMovementUseCase {
    Movement execute(String numeroCuenta, TipoCuenta tipoCuenta, Boolean estado,
                     TipoMovimiento tipoMovimiento, BigDecimal valor);
}
