package com.sofka.ms_account_service.infrastructure.adapter.in.web.shared;

public record ApiResponse<T>(
        int statusCode,
        String status,
        String message,
        T data
) {}
