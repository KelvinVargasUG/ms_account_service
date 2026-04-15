package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateMovementUseCase {
    Movement execute(UUID id, TipoMovimiento tipoMovimiento, BigDecimal valor);
}
