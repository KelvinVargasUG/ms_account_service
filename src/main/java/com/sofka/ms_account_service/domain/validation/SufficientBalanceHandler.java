package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.strategy.MovementStrategy;
import com.sofka.ms_account_service.domain.strategy.MovementStrategyFactory;

import java.math.BigDecimal;

public class SufficientBalanceHandler extends MovementValidationHandler {

    @Override
    protected void doHandle(MovementValidationContext context) {
        MovementStrategy strategy = MovementStrategyFactory.resolve(
                context.getTipoMovimiento());
        BigDecimal efectivo = strategy.calculateEffectiveAmount(context.getValor());
        BigDecimal nuevoSaldo = context.getAccount().getSaldo().add(efectivo);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
    }
}
