package com.sofka.ms_account_service.infrastructure.adapter.in.kafka;

import com.sofka.ms_account_service.domain.model.Account;
import com.sofka.ms_account_service.domain.model.CustomerSnapshot;
import com.sofka.ms_account_service.domain.port.out.AccountRepositoryPort;
import com.sofka.ms_account_service.domain.port.out.CustomerSnapshotRepositoryPort;
import com.sofka.ms_account_service.infrastructure.adapter.in.kafka.dto.CustomerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerKafkaConsumer {

    private final CustomerSnapshotRepositoryPort snapshotRepository;
    private final AccountRepositoryPort accountRepository;

    @KafkaListener(
            topics = "${kafka.topics.customer-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(CustomerEvent event) {
        if (event == null || event.getClienteId() == null) {
            log.warn("Evento de cliente inválido recibido, se descarta.");
            return;
        }

        log.info("Evento recibido: type={}, clienteId={}", event.getEventType(), event.getClienteId());

        switch (event.getEventType()) {
            case CREATED, UPDATED -> {
                CustomerSnapshot snapshot = new CustomerSnapshot(
                        event.getClienteId(),
                        event.getNombre(),
                        event.getActivo()
                );
                snapshotRepository.save(snapshot);
                log.info("Snapshot upserted para clienteId={}", event.getClienteId());
                syncAccountEstado(event.getClienteId(), Boolean.TRUE.equals(event.getActivo()));
            }
            case DELETED -> {
                snapshotRepository.deleteByClienteId(event.getClienteId());
                log.info("Snapshot eliminado para clienteId={}", event.getClienteId());
                syncAccountEstado(event.getClienteId(), false);
            }
        }
    }

    private void syncAccountEstado(java.util.UUID clienteId, boolean activo) {
        List<Account> accounts = accountRepository.findByClienteId(clienteId);
        if (accounts.isEmpty()) {
            return;
        }
        accounts.forEach(a -> a.updateEstado(activo));
        accountRepository.saveAll(accounts);
        log.info("Cuentas {} para clienteId={} (total={})",
                activo ? "activadas" : "desactivadas", clienteId, accounts.size());
    }
}
