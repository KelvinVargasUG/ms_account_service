package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.adapter;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper.AccountMapper;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Account> findByClienteId(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Account save(Account account) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(account)));
    }

    @Override
    public List<Account> saveAll(List<Account> accounts) {
        return jpaRepository.saveAll(accounts.stream().map(mapper::toEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }
}
