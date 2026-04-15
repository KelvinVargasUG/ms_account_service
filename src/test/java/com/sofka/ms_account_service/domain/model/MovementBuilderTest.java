package com.sofka.ms_account_service.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MovementBuilderTest {

    @Test
    void shouldBuildMovementWithAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();
        UUID cuentaId = UUID.randomUUID();

        Movement movement = Movement.builder()
                .id(id)
                .fecha(fecha)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.valueOf(500))
                .saldo(BigDecimal.valueOf(1500))
                .cuentaId(cuentaId)
                .build();

        assertThat(movement.getId()).isEqualTo(id);
        assertThat(movement.getFecha()).isEqualTo(fecha);
        assertThat(movement.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(movement.getValor()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(movement.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        assertThat(movement.getCuentaId()).isEqualTo(cuentaId);
    }

    @Test
    void shouldBuildMovementWithPartialFields() {
        Movement movement = Movement.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(BigDecimal.valueOf(100))
                .build();

        assertThat(movement.getId()).isNull();
        assertThat(movement.getFecha()).isNull();
        assertThat(movement.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(movement.getValor()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(movement.getSaldo()).isNull();
        assertThat(movement.getCuentaId()).isNull();
    }

    @Test
    void shouldBuildAccountWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Account account = Account.builder()
                .id(id)
                .numeroCuenta("1234567890")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.valueOf(1000))
                .saldo(BigDecimal.valueOf(1000))
                .estado(true)
                .clienteId(clienteId)
                .build();

        assertThat(account.getId()).isEqualTo(id);
        assertThat(account.getNumeroCuenta()).isEqualTo("1234567890");
        assertThat(account.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        assertThat(account.getSaldoInicial()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(account.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(account.getEstado()).isTrue();
        assertThat(account.getClienteId()).isEqualTo(clienteId);
    }

    @Test
    void shouldBuildAccountWithPartialFields() {
        Account account = Account.builder()
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .estado(false)
                .build();

        assertThat(account.getId()).isNull();
        assertThat(account.getNumeroCuenta()).isNull();
        assertThat(account.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(account.getEstado()).isFalse();
    }
}
