package com.sofka.ms_account_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {

    @Setter
    private UUID id;
    private LocalDateTime fecha;
    @Setter
    private TipoMovimiento tipoMovimiento;
    @Setter
    private BigDecimal valor;
    private BigDecimal saldo;
    private UUID cuentaId;

    public static Movement create(BigDecimal valor, BigDecimal saldoResultante, UUID cuentaId) {
        TipoMovimiento tipo = valor.compareTo(BigDecimal.ZERO) >= 0
                ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO;
        return builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipo)
                .valor(valor)
                .saldo(saldoResultante)
                .cuentaId(cuentaId)
                .build();
    }

    public static Movement create(TipoMovimiento tipoMovimiento, BigDecimal valor,
                                   BigDecimal saldoResultante, UUID cuentaId) {
        return builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(valor)
                .saldo(saldoResultante)
                .cuentaId(cuentaId)
                .build();
    }

}
