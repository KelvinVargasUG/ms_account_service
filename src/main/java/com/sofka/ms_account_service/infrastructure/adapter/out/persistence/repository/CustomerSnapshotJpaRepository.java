package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.repository;

import com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity.CustomerSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerSnapshotJpaRepository extends JpaRepository<CustomerSnapshotEntity, UUID> {
}
