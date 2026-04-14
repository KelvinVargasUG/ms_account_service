package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.InactiveAccountException;
import com.sofka.ms_account_service.domain.exception.InsufficientBalanceException;
import com.sofka.ms_account_service.domain.exception.InvalidClientException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class RegisterMovementUseCaseImpl implements RegisterMovementUseCase {

    private final AccountRepositoryPort accountRepository;
    private final MovementRepositoryPort movementRepository;
    private final CustomerClientPort customerClient;

    @Override
    @Transactional
    public Movement execute(String numeroCuenta, TipoCuenta tipoCuenta, Boolean estado,
                            TipoMovimiento tipoMovimiento, BigDecimal valor) {
        Account account = accountRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        if (!customerClient.isClientActive(account.getClienteId())) {
            throw new InvalidClientException("El cliente no está activo: " + account.getClienteId());
        }
        if (!account.getEstado()) {
            throw new InactiveAccountException("La cuenta está inactiva: " + numeroCuenta);
        }
        BigDecimal efectivo = tipoMovimiento == TipoMovimiento.RETIRO ? valor.negate() : valor;
        BigDecimal nuevoSaldo = account.getSaldo().add(efectivo);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
        account.applyMovement(efectivo);
        accountRepository.save(account);
        Movement movement = Movement.create(tipoMovimiento, valor, account.getSaldo(), account.getId());
        return movementRepository.save(movement);
    }
}
