package com.sofka.ms_account_service.infrastructure.adapter.in.kafka.dto;

import java.util.UUID;

public class CustomerEvent {

    public enum EventType {
        CREATED, UPDATED, DELETED
    }

    private UUID clienteId;
    private String nombre;
    private Boolean activo;
    private EventType eventType;

    public CustomerEvent() {}

    public CustomerEvent(UUID clienteId, String nombre, Boolean activo, EventType eventType) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.activo = activo;
        this.eventType = eventType;
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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
