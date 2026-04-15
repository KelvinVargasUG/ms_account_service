package com.sofka.ms_account_service.domain.strategy;

import com.sofka.ms_account_service.domain.model.TipoMovimiento;

import java.util.EnumMap;
import java.util.Map;

public final class MovementStrategyFactory {

    private static final Map<TipoMovimiento, MovementStrategy> STRATEGIES =
            new EnumMap<>(TipoMovimiento.class);

    static {
        STRATEGIES.put(TipoMovimiento.DEPOSITO, new DepositoStrategy());
        STRATEGIES.put(TipoMovimiento.RETIRO, new RetiroStrategy());
    }

    private MovementStrategyFactory() {
    }

    public static MovementStrategy resolve(TipoMovimiento tipo) {
        MovementStrategy strategy = STRATEGIES.get(tipo);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Tipo de movimiento no soportado: " + tipo);
        }
        return strategy;
    }
}
