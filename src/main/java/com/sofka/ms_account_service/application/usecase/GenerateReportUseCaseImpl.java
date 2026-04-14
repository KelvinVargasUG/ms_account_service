package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.ReportEntry;
import com.sofka.ms_account_service.domain.port.in.GenerateReportUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GenerateReportUseCaseImpl implements GenerateReportUseCase {

    private final AccountRepositoryPort accountRepository;
    private final MovementRepositoryPort movementRepository;
    private final CustomerClientPort customerClient;

    @Override
    public List<ReportEntry> execute(String numeroCuenta, LocalDate start, LocalDate end) {
        final List<Account> accounts;

        if (numeroCuenta != null && !numeroCuenta.isBlank()) {
            Account origin = accountRepository.findByNumeroCuenta(numeroCuenta)
                    .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada: " + numeroCuenta));
            UUID clienteId = origin.getClienteId();
            if (!customerClient.isClientActive(clienteId)) {
                throw new InvalidClientException("El cliente no está activo: " + clienteId);
            }
            accounts = accountRepository.findByClienteId(clienteId);
        } else {
            accounts = accountRepository.findAll();
        }

        List<UUID> accountIds = accounts.stream().map(Account::getId).toList();
        Map<UUID, Account> accountMap = accounts.stream()
                .collect(Collectors.toMap(Account::getId, a -> a));

        LocalDateTime effectiveStart = start != null ? start.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime effectiveEnd = end != null ? end.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Movement> movements = movementRepository
                .findByAccountIdsAndDateRange(accountIds, effectiveStart, effectiveEnd);

        return movements.stream().map(m -> {
            Account acc = accountMap.get(m.getCuentaId());
            UUID clienteId = acc.getClienteId();
            String clienteName = customerClient.getClienteName(clienteId);
            return new ReportEntry(
                    clienteName,
                    acc.getNumeroCuenta(),
                    acc.getTipoCuenta().name(),
                    acc.getSaldoInicial(),
                    acc.getEstado(),
                    m.getFecha(),
                    m.getTipoMovimiento().name(),
                    m.getValor(),
                    m.getSaldo()
            );
        }).toList();
    }
}
