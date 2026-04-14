package com.sofka.ms_account_service.infrastructure.adapter.in.web.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {

    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_ERROR = "error";

    private ResponseUtil() {}

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(200, STATUS_SUCCESS, "OK", data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(200, STATUS_SUCCESS, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, STATUS_SUCCESS, "Created", data));
    }

    public static ResponseEntity<ApiResponse<Void>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(204, STATUS_SUCCESS, "No Content", null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(int statusCode, String message) {
        return ResponseEntity.status(statusCode)
                .body(new ApiResponse<>(statusCode, STATUS_ERROR, message, null));
    }
}
