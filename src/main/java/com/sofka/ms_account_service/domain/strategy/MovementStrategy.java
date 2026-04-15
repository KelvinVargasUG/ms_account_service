package com.sofka.ms_account_service.domain.strategy;

import java.math.BigDecimal;

public interface MovementStrategy {

    BigDecimal calculateEffectiveAmount(BigDecimal valor);
}
