package com.sofka.ms_account_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSnapshot {

    private UUID clienteId;
    private String nombre;
    private Boolean activo;
}
