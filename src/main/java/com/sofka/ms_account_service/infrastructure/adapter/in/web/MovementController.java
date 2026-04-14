package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementRequest;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ApiResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Movimientos", description = "Registro de movimientos (débitos y créditos)")
@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovementController {

    private final RegisterMovementUseCase registerMovementUseCase;

    @Operation(summary = "Registrar movimiento", description = "Registra un débito o crédito en una cuenta")
    @PostMapping
    public ResponseEntity<ApiResponse<MovementResponse>> register(@Valid @RequestBody MovementRequest request) {
        Movement movement = registerMovementUseCase.execute(
                request.numeroCuenta(), request.tipoCuenta(), request.estado(),
                request.tipoMovimiento(), request.valor());
        return ResponseUtil.created(toResponse(movement));
    }

    private MovementResponse toResponse(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getFecha(),
                movement.getTipoMovimiento(),
                movement.getValor(),
                movement.getSaldo(),
                movement.getCuentaId()
        );
    }
}
