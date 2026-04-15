package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.exception.InactiveAccountException;

public class AccountActiveHandler extends MovementValidationHandler {

    @Override
    protected void doHandle(MovementValidationContext context) {
        if (!context.getAccount().getEstado()) {
            throw new InactiveAccountException(
                    "La cuenta está inactiva: " + context.getAccount().getNumeroCuenta());
        }
    }
}
