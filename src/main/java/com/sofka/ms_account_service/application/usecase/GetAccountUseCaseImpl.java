package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.port.in.GetAccountUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GetAccountUseCaseImpl implements GetAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final CustomerClientPort customerClient;

    @Override
    public Account findById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con id: " + id));
        if (!customerClient.isClientActive(account.getClienteId())) {
            throw new InvalidClientException("El cliente no está activo: " + account.getClienteId());
        }
        return account;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll().stream()
                .filter(a -> customerClient.isClientActive(a.getClienteId()))
                .toList();
    }
}
