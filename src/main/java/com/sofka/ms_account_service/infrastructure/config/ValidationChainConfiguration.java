package com.sofka.ms_account_service.infrastructure.config;

import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerClientPort;
import com.sofka.ms_account_service.domain.validation.AccountActiveHandler;
import com.sofka.ms_account_service.domain.validation.AccountExistsHandler;
import com.sofka.ms_account_service.domain.validation.ClientActiveHandler;
import com.sofka.ms_account_service.domain.validation.MovementValidationHandler;
import com.sofka.ms_account_service.domain.validation.SufficientBalanceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ValidationChainConfiguration {

    private final AccountRepositoryPort accountRepository;
    private final CustomerClientPort customerClient;

    @Bean
    public MovementValidationHandler movementValidationChain() {
        AccountExistsHandler accountExists = new AccountExistsHandler(accountRepository);
        ClientActiveHandler clientActive = new ClientActiveHandler(customerClient);
        AccountActiveHandler accountActive = new AccountActiveHandler();
        SufficientBalanceHandler sufficientBalance = new SufficientBalanceHandler();

        accountExists.setNext(clientActive);
        clientActive.setNext(accountActive);
        accountActive.setNext(sufficientBalance);

        return accountExists;
    }
}
