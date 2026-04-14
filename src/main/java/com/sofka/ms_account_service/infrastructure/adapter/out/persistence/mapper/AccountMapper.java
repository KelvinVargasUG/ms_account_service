package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.mapper;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    public Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getNumeroCuenta(),
                entity.getTipoCuenta(),
                entity.getSaldoInicial(),
                entity.getSaldo(),
                entity.getEstado(),
                entity.getClienteId()
        );
    }

    public AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setNumeroCuenta(account.getNumeroCuenta());
        entity.setTipoCuenta(account.getTipoCuenta());
        entity.setSaldoInicial(account.getSaldoInicial());
        entity.setSaldo(account.getSaldo());
        entity.setEstado(account.getEstado());
        entity.setClienteId(account.getClienteId());
        return entity;
    }
}
