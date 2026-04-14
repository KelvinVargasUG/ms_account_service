package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.in.UpdateAccountUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateAccountUseCaseImpl implements UpdateAccountUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    public Account execute(UUID id, TipoCuenta tipoCuenta, Boolean estado) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con id: " + id));
        if (tipoCuenta != null && tipoCuenta != account.getTipoCuenta()) {
            account.updateTipoCuenta(tipoCuenta);
        }
        if (estado != null) {
            account.updateEstado(estado);
        }
        return accountRepository.save(account);
    }


}
