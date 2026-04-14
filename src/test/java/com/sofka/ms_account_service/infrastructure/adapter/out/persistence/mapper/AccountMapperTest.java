package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    AccountMapper mapper;

    final UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID clienteId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        mapper = new AccountMapper();
    }

    @Test
    void shouldMapEntityToDomain() {
        AccountEntity entity = new AccountEntity();
        entity.setId(id);
        entity.setNumeroCuenta("1234567890");
        entity.setTipoCuenta(TipoCuenta.AHORRO);
        entity.setSaldoInicial(BigDecimal.valueOf(500));
        entity.setSaldo(BigDecimal.valueOf(450));
        entity.setEstado(true);
        entity.setClienteId(clienteId);

        Account domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getNumeroCuenta()).isEqualTo("1234567890");
        assertThat(domain.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        assertThat(domain.getSaldoInicial()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(domain.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(450));
        assertThat(domain.getEstado()).isTrue();
        assertThat(domain.getClienteId()).isEqualTo(clienteId);
    }

    @Test
    void shouldMapDomainToEntity() {
        Account domain = new Account(id, "9876543210", TipoCuenta.CORRIENTE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(900), true, clienteId);

        AccountEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNumeroCuenta()).isEqualTo("9876543210");
        assertThat(entity.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(entity.getSaldoInicial()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(entity.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(900));
        assertThat(entity.getEstado()).isTrue();
        assertThat(entity.getClienteId()).isEqualTo(clienteId);
    }
}
