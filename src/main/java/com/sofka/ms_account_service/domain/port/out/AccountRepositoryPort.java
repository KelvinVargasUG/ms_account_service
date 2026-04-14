package com.sofka.ms_account_service.domain.port.out;

import com.sofka.ms_account_service.domain.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Optional<Account> findById(UUID id);
    Optional<Account> findByNumeroCuenta(String numeroCuenta);
    List<Account> findAll();
    List<Account> findByClienteId(UUID clienteId);
    Account save(Account account);
    List<Account> saveAll(List<Account> accounts);
}
