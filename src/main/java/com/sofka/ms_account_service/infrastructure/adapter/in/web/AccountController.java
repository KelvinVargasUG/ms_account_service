package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.port.in.CreateAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.DeleteAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.GetAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.UpdateAccountUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.AccountRequest;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.AccountResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.AccountUpdateRequest;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ApiResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cuentas", description = "Operaciones sobre cuentas bancarias")
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;

    @Operation(summary = "Crear cuenta", description = "Registra una nueva cuenta bancaria")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> create(@Valid @RequestBody AccountRequest request) {
        Account account = createAccountUseCase.execute(
                request.tipoCuenta(), request.saldoInicial(), request.clienteId());
        return ResponseUtil.created(toResponse(account));
    }

    @Operation(summary = "Listar cuentas", description = "Obtiene todas las cuentas registradas")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> findAll() {
        List<AccountResponse> accounts = getAccountUseCase.findAll().stream()
                .map(this::toResponse).toList();
        return ResponseUtil.success(accounts);
    }

    @Operation(summary = "Obtener cuenta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> findById(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id) {
        return ResponseUtil.success(toResponse(getAccountUseCase.findById(id)));
    }

    @Operation(summary = "Actualizar cuenta", description = "Actualiza tipo y estado de una cuenta")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> update(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id,
            @RequestBody AccountUpdateRequest request) {
        Account account = updateAccountUseCase.execute(id, request.tipoCuenta(), request.estado());
        return ResponseUtil.success(toResponse(account));
    }

    @Operation(summary = "Eliminar cuenta")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID de la cuenta") @PathVariable UUID id) {
        deleteAccountUseCase.execute(id);
        return ResponseUtil.success("Cuenta eliminada correctamente", null);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getNumeroCuenta(),
                account.getTipoCuenta(),
                account.getSaldoInicial(),
                account.getSaldo(),
                account.getEstado(),
                account.getClienteId()
        );
    }
}
