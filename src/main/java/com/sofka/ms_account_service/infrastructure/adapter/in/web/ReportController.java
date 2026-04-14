package com.sofka.ms_account_service.infrastructure.adapter.in.web;

import com.sofka.ms_account_service.domain.model.ReportEntry;
import com.sofka.ms_account_service.domain.port.in.GenerateReportUseCase;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ApiResponse;
import com.sofka.ms_account_service.infrastructure.adapter.in.web.shared.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reportes", description = "Generación de reportes de movimientos por cliente y rango de fechas")
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final GenerateReportUseCase generateReportUseCase;

    @Operation(summary = "Generar reporte", description = "Retorna movimientos de un cliente en un rango de fechas")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportEntry>>> getReport(
            @Parameter(description = "Número de cuenta (opcional)") @RequestParam(required = false) String numeroCuenta,
            @Parameter(description = "Fecha inicio (yyyy-MM-dd, opcional)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd, opcional)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<ReportEntry> report = generateReportUseCase.execute(numeroCuenta, fechaInicio, fechaFin);
        return ResponseUtil.success(report);
    }
}
