package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.AccountMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryAdapterTest {

    @Mock
    AccountJpaRepository jpaRepository;

    @Mock
    AccountMapper mapper;

    AccountRepositoryAdapter adapter;

    final UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final UUID clienteId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    AccountEntity entity;
    Account domain;

    @BeforeEach
    void setUp() {
        adapter = new AccountRepositoryAdapter(jpaRepository, mapper);

        entity = new AccountEntity();
        entity.setId(id);
        entity.setNumeroCuenta("1234567890");
        entity.setTipoCuenta(TipoCuenta.AHORRO);
        entity.setSaldoInicial(BigDecimal.valueOf(500));
        entity.setSaldo(BigDecimal.valueOf(500));
        entity.setEstado(true);
        entity.setClienteId(clienteId);

        domain = new Account(id, "1234567890", TipoCuenta.AHORRO,
                BigDecimal.valueOf(500), BigDecimal.valueOf(500), true, clienteId);
    }

    @Test
    void shouldFindById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Account> result = adapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Account> result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByNumeroCuenta() {
        when(jpaRepository.findByNumeroCuenta("1234567890")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Account> result = adapter.findByNumeroCuenta("1234567890");

        assertThat(result).isPresent();
        assertThat(result.get().getNumeroCuenta()).isEqualTo("1234567890");
    }

    @Test
    void shouldFindAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Account> result = adapter.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindByClienteId() {
        when(jpaRepository.findByClienteId(clienteId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Account> result = adapter.findByClienteId(clienteId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClienteId()).isEqualTo(clienteId);
    }

    @Test
    void shouldSave() {
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Account result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldSaveAll() {
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.saveAll(List.of(entity))).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Account> result = adapter.saveAll(List.of(domain));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(id);
    }
}
