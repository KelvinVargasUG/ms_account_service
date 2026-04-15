package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.port.in.DeleteMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.GetMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.UpdateMovementUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementRequest;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.dto.MovementUpdateRequest;
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

@Tag(name = "Movimientos", description = "Registro de movimientos (débitos y créditos)")
@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovementController {

    private final RegisterMovementUseCase registerMovementUseCase;
    private final GetMovementUseCase getMovementUseCase;
    private final UpdateMovementUseCase updateMovementUseCase;
    private final DeleteMovementUseCase deleteMovementUseCase;

    @Operation(summary = "Registrar movimiento", description = "Registra un débito o crédito en una cuenta")
    @PostMapping
    public ResponseEntity<ApiResponse<MovementResponse>> register(
            @Valid @RequestBody MovementRequest request) {
        Movement movement = registerMovementUseCase.execute(
                request.numeroCuenta(), request.tipoCuenta(), request.estado(),
                request.tipoMovimiento(), request.valor());
        return ResponseUtil.created(toResponse(movement));
    }

    @Operation(summary = "Listar movimientos", description = "Obtiene todos los movimientos registrados")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovementResponse>>> findAll() {
        List<MovementResponse> movements = getMovementUseCase.findAll().stream()
                .map(this::toResponse).toList();
        return ResponseUtil.success(movements);
    }

    @Operation(summary = "Obtener movimiento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovementResponse>> findById(
            @Parameter(description = "ID del movimiento") @PathVariable UUID id) {
        return ResponseUtil.success(toResponse(getMovementUseCase.findById(id)));
    }

    @Operation(summary = "Actualizar movimiento",
            description = "Actualiza tipo de movimiento y/o valor de un registro existente")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovementResponse>> update(
            @Parameter(description = "ID del movimiento") @PathVariable UUID id,
            @RequestBody MovementUpdateRequest request) {
        Movement movement = updateMovementUseCase.execute(
                id, request.tipoMovimiento(), request.valor());
        return ResponseUtil.success(toResponse(movement));
    }

    @Operation(summary = "Eliminar movimiento")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID del movimiento") @PathVariable UUID id) {
        deleteMovementUseCase.execute(id);
        return ResponseUtil.success("Movimiento eliminado correctamente", null);
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
