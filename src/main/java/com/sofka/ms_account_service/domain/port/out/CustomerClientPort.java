package com.sofka.ms_account_service.domain.port.out;

import java.util.UUID;

public interface CustomerClientPort {
    boolean isClientActive(UUID clienteId);
    String getClienteName(UUID clienteId);
}
