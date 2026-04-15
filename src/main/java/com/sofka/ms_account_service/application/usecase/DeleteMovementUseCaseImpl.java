package com.sofka.ms_account_service.application.usecase;

import com.sofka.ms_account_service.domain.exception.AccountNotFoundException;
import com.sofka.ms_account_service.domain.exception.MovementNotFoundException;
import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.Movement;
import com.sofka.ms_account_service.domain.model.TipoMovimiento;
import com.sofka.ms_account_service.domain.port.in.DeleteMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import com.sofka.ms_account_service.domain.strategy.MovementStrategyFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class DeleteMovementUseCaseImpl implements DeleteMovementUseCase {

    private final MovementRepositoryPort movementRepository;
    private final AccountRepositoryPort accountRepository;

    @Override
    public Movement execute(UUID id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(
                        "Movimiento no encontrado con id: " + id));

        Account account = accountRepository.findById(movement.getCuentaId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Cuenta no encontrada con id: " + movement.getCuentaId()));

        TipoMovimiento reversalType = movement.getTipoMovimiento() == TipoMovimiento.DEPOSITO
                ? TipoMovimiento.RETIRO : TipoMovimiento.DEPOSITO;

        BigDecimal reversalEffective = MovementStrategyFactory
                .resolve(reversalType)
                .calculateEffectiveAmount(movement.getValor());
        account.applyMovement(reversalEffective);
        accountRepository.save(account);

        Movement reversal = Movement.create(
                reversalType, movement.getValor(), account.getSaldo(), account.getId());
        Movement savedReversal = movementRepository.save(reversal);

        movementRepository.deleteById(id);

        return savedReversal;
    }
}
