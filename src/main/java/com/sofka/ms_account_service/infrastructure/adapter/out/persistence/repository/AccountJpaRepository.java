package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository;

import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByNumeroCuenta(String numeroCuenta);
    List<AccountEntity> findByClienteId(UUID clienteId);
}
