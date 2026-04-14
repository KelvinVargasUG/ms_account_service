package com.sofka.ms_account_service.domain.port.in;

import com.sofka.ms_account_service.domain.model.ReportEntry;

import java.time.LocalDate;
import java.util.List;

public interface GenerateReportUseCase {
    List<ReportEntry> execute(String numeroCuenta, LocalDate start, LocalDate end);
}
