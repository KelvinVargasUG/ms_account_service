package com.sofka.ms_account_service.domain.validation;

import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;

public class ClientActiveHandler extends MovementValidationHandler {

    private final CustomerClientPort customerClient;

    public ClientActiveHandler(CustomerClientPort customerClient) {
        this.customerClient = customerClient;
    }

    @Override
    protected void doHandle(MovementValidationContext context) {
        if (!customerClient.isClientActive(context.getAccount().getClienteId())) {
            throw new InvalidClientException(
                    "El cliente no está activo: " + context.getAccount().getClienteId());
        }
    }
}
