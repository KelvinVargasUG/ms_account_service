package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.port.in.CreateAccountUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final CustomerClientPort customerClient;

    @Override
    public Account execute(TipoCuenta tipoCuenta, BigDecimal saldoInicial, UUID clienteId) {
        if (!customerClient.isClientActive(clienteId)) {
            throw new InvalidClientException(
                    "El clienteId no es válido o el cliente está inactivo: " + clienteId);
        }
        Account account = Account.create(tipoCuenta, saldoInicial, clienteId);
        return accountRepository.save(account);
    }
}
