package com.sofka.ms_account_service.infrastructure.adapter.in.web.dto;

import com.sofka.ms_account_service.domain.model.TipoCuenta;

public record AccountUpdateRequest(
        TipoCuenta tipoCuenta,
        Boolean estado
) {}
