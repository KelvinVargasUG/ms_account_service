package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoCuenta;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import com.sofka.ms_account_service.domain.strategy.MovementStrategy;
import com.sofka.ms_account_service.domain.strategy.MovementStrategyFactory;
import com.sofka.ms_account_service.domain.validation.MovementValidationContext;
import com.sofka.ms_account_service.domain.validation.MovementValidationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RegisterMovementUseCaseImpl implements RegisterMovementUseCase {

    private final MovementValidationHandler validationChain;
    private final AccountRepositoryPort accountRepository;
    private final MovementRepositoryPort movementRepository;

    @Override
    @Transactional
    public Movement execute(String numeroCuenta, TipoCuenta tipoCuenta, Boolean estado,
                            TipoMovimiento tipoMovimiento, BigDecimal valor) {
        MovementValidationContext context =
                new MovementValidationContext(numeroCuenta, tipoMovimiento, valor);
        validationChain.handle(context);

        Account account = context.getAccount();
        MovementStrategy strategy = MovementStrategyFactory.resolve(tipoMovimiento);
        BigDecimal efectivo = strategy.calculateEffectiveAmount(valor);

        account.applyMovement(efectivo);
        accountRepository.save(account);

        Movement movement = Movement.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(valor)
                .saldo(account.getSaldo())
                .cuentaId(account.getId())
                .build();

        return movementRepository.save(movement);
    }
}
