package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.Account;

import java.util.List;
import java.util.UUID;

public interface GetAccountUseCase {
    Account findById(UUID id);
    List<Account> findAll();
}
