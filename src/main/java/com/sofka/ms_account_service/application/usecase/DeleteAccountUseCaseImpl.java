package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.port.in.DeleteAccountUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    public void execute(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con id: " + id));
        if (Boolean.FALSE.equals(account.getEstado())) {
            throw new InactiveAccountException("La cuenta ya fue eliminada: " + id);
        }
        account.deactivate();
        accountRepository.save(account);
    }
}
