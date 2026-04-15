package com.sofka.ms_account_service.domain.strategy;

import java.math.BigDecimal;

public class DepositoStrategy implements MovementStrategy {

    @Override
    public BigDecimal calculateEffectiveAmount(BigDecimal valor) {
        return valor;
    }
}
