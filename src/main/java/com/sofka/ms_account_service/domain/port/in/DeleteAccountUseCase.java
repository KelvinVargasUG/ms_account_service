package com.sofka.ms_account_service.domain.port.in;

import java.util.UUID;

public interface DeleteAccountUseCase {
    void execute(UUID id);
}
