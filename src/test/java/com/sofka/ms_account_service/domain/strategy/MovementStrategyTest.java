package com.sofka.ms_account_service.domain.strategy;

import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovementStrategyTest {

    @Test
    void depositoStrategyShouldReturnSameAmount() {
        MovementStrategy strategy = new DepositoStrategy();
        BigDecimal result = strategy.calculateEffectiveAmount(BigDecimal.valueOf(500));
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void retiroStrategyShouldReturnNegatedAmount() {
        MovementStrategy strategy = new RetiroStrategy();
        BigDecimal result = strategy.calculateEffectiveAmount(BigDecimal.valueOf(200));
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(-200));
    }

    @Test
    void factoryShouldResolveDeposito() {
        MovementStrategy strategy = MovementStrategyFactory.resolve(TipoMovimiento.DEPOSITO);
        assertThat(strategy).isInstanceOf(DepositoStrategy.class);
    }

    @Test
    void factoryShouldResolveRetiro() {
        MovementStrategy strategy = MovementStrategyFactory.resolve(TipoMovimiento.RETIRO);
        assertThat(strategy).isInstanceOf(RetiroStrategy.class);
    }

    @Test
    void factoryShouldThrowForNull() {
        assertThatThrownBy(() -> MovementStrategyFactory.resolve(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
