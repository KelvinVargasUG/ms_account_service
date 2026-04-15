package com.sofka.ms_account_service.domain.model;

import java.util.UUID;

public class CustomerSnapshot {

    private UUID clienteId;
    private String nombre;
    private Boolean activo;

    public CustomerSnapshot() {
    }

    public CustomerSnapshot(UUID clienteId, String nombre, Boolean activo) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.activo = activo;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
