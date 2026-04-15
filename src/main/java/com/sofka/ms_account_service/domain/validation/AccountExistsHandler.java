package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;

public class AccountExistsHandler extends MovementValidationHandler {

    private final AccountRepositoryPort accountRepository;

    public AccountExistsHandler(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doHandle(MovementValidationContext context) {
        Account account = accountRepository
                .findByNumeroCuenta(context.getNumeroCuenta())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Cuenta no encontrada: " + context.getNumeroCuenta()));
        context.setAccount(account);
    }
}
