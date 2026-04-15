package com.sofka.ms_account_service.infrastructure.config;

import com.sofka.ms_account_service.application.usecase.CreateAccountUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.DeleteAccountUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.DeleteMovementUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.GenerateReportUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.GetAccountUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.GetMovementUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.RegisterMovementUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.UpdateAccountUseCaseImpl;
import com.sofka.ms_account_service.application.usecase.UpdateMovementUseCaseImpl;
import com.sofka.ms_account_service.domain.port.in.CreateAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.DeleteAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.DeleteMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.GenerateReportUseCase;
import com.sofka.ms_account_service.domain.port.in.GetAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.GetMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.RegisterMovementUseCase;
import com.sofka.ms_account_service.domain.port.in.UpdateAccountUseCase;
import com.sofka.ms_account_service.domain.port.in.UpdateMovementUseCase;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.port.out.MovementRepositoryPort;
import com.sofka.ms_account_service.domain.validation.MovementValidationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final AccountRepositoryPort accountRepository;
    private final MovementRepositoryPort movementRepository;
    private final CustomerClientPort customerClient;
    private final MovementValidationHandler movementValidationChain;

    @Bean
    public CreateAccountUseCase createAccountUseCase() {
        return new CreateAccountUseCaseImpl(accountRepository, customerClient);
    }

    @Bean
    public GetAccountUseCase getAccountUseCase() {
        return new GetAccountUseCaseImpl(accountRepository, customerClient);
    }

    @Bean
    public UpdateAccountUseCase updateAccountUseCase() {
        return new UpdateAccountUseCaseImpl(accountRepository);
    }

    @Bean
    public DeleteAccountUseCase deleteAccountUseCase() {
        return new DeleteAccountUseCaseImpl(accountRepository);
    }

    @Bean
    public RegisterMovementUseCase registerMovementUseCase() {
        return new RegisterMovementUseCaseImpl(
                movementValidationChain, accountRepository, movementRepository);
    }

    @Bean
    public GenerateReportUseCase generateReportUseCase() {
        return new GenerateReportUseCaseImpl(
                accountRepository, movementRepository, customerClient);
    }

    @Bean
    public GetMovementUseCase getMovementUseCase() {
        return new GetMovementUseCaseImpl(movementRepository);
    }

    @Bean
    public UpdateMovementUseCase updateMovementUseCase() {
        return new UpdateMovementUseCaseImpl(movementRepository);
    }

    @Bean
    public DeleteMovementUseCase deleteMovementUseCase() {
        return new DeleteMovementUseCaseImpl(movementRepository);
    }
}
