package com.sofka.ms_account_service.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "clientes_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class CustomerSnapshotEntity {

    @Id
    @Column(name = "id_clientes_snapshot")
    private UUID clienteId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo;

    public CustomerSnapshotEntity(UUID clienteId, String nombre, Boolean activo) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.activo = activo;
    }
}
