# Casos de Prueba — Account & Movement Service Management

| Campo    | Detalle                                                      |
| -------- | ------------------------------------------------------------ |
| Versión  | 1.1                                                          |
| Fecha    | 14/04/2026                                                   |
| Estado   | Activo                                                       |
| Proyecto | Plataforma-Bancaria-Account-Service                          |
| Área     | QA                                                           |

---

## HU-CM01: Creación de una nueva cuenta bancaria

---

### Escenario 1 — TC-HUCM01-01: Creación exitosa de cuenta bancaria

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que una cuenta bancaria se crea correctamente con todos sus campos y estado activo        |
| **Técnica**     | Partición de equivalencia — datos válidos                                                           |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | El repositorio está disponible; no existe cuenta con el mismo `numeroCuenta`; el `clienteId` corresponde a un cliente activo en `ms-customer-service` |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que no existe una cuenta con numeroCuenta "1234567890" en el sistema
And el clienteId "C001" corresponde a un cliente activo
When el administrador envía POST /cuentas con numeroCuenta "1234567890", tipoCuenta "AHORRO", saldoInicial 1000.00, estado true y clienteId "C001"
Then el sistema crea la cuenta exitosamente
And el sistema devuelve código de estado 201 Created
And la respuesta contiene los datos de la cuenta creada con numeroCuenta "1234567890" y saldoInicial 1000.00
And la cuenta queda persistida en la base de datos con estado activo
```

**Resultado esperado:** La cuenta bancaria queda registrada con todos sus campos correctamente y el sistema devuelve `201 Created` con los datos de la cuenta creada.

---

### Escenario 2 — TC-HUCM01-02: Intento de creación con numeroCuenta duplicado

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza la creación de una cuenta con un `numeroCuenta` ya registrado      |
| **Técnica**     | Prueba negativa — unicidad de dato clave                                                            |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con `numeroCuenta` "1234567890" registrada en la base de datos               |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que ya existe una cuenta con numeroCuenta "1234567890" en el sistema
When el administrador envía POST /cuentas con numeroCuenta "1234567890" y datos válidos de otra cuenta
Then el sistema rechaza la solicitud
And el sistema devuelve un código de error (4xx)
And no se crea ningún registro duplicado en la base de datos
```

**Resultado esperado:** El sistema devuelve un error al intentar registrar un `numeroCuenta` ya existente. La base de datos permanece íntegra sin duplicados.

---

### Escenario 3 — TC-HUCM01-03: Creación con clienteId inválido o inexistente

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza la creación de una cuenta cuando el `clienteId` no existe o no es válido |
| **Técnica**     | Prueba negativa — validación de referencia externa                                                  |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | El `clienteId` "C999" no existe en `ms-customer-service`                                        |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que el clienteId "C999" no existe en el sistema de clientes
When el administrador envía POST /cuentas con clienteId "C999" y datos de cuenta válidos
Then el sistema rechaza la solicitud
And el sistema devuelve código de estado 400 Bad Request
And no se crea ninguna cuenta en la base de datos
```

**Resultado esperado:** El sistema devuelve `400 Bad Request` y no persiste ninguna cuenta cuando el `clienteId` no corresponde a un cliente existente.

---

### Escenario 4 — TC-HUCM01-04: Creación con clienteId de cliente inactivo

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza la creación de una cuenta asociada a un cliente inactivo           |
| **Técnica**     | Prueba basada en estado — cliente inactivo                                                          |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | El `clienteId` "C002" existe en `ms-customer-service` con estado inactivo (`estado = false`)    |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que el clienteId "C002" corresponde a un cliente con estado inactivo
When el administrador envía POST /cuentas con clienteId "C002" y datos de cuenta válidos
Then el sistema rechaza la solicitud
And el sistema devuelve código de estado 400 Bad Request
And no se crea ninguna cuenta en la base de datos
```

**Resultado esperado:** El sistema devuelve `400 Bad Request` y no crea la cuenta cuando el cliente asociado está inactivo.

---

## HU-CM02: Gestión de cuentas (CRUD)

---

### Escenario 1 — TC-HUCM02-01: Consulta exitosa de cuenta por ID

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema devuelve correctamente los datos de una cuenta existente consultada por ID |
| **Técnica**     | Partición de equivalencia — dato válido existente                                                   |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con ID "ACC001" registrada en la base de datos                               |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta con ID "ACC001" en el sistema
When el administrador envía GET /cuentas/ACC001
Then el sistema devuelve código de estado 200 OK
And la respuesta contiene los datos completos de la cuenta con ID "ACC001"
And el campo saldo refleja el saldo actual de la cuenta
```

**Resultado esperado:** El sistema devuelve `200 OK` con los datos completos de la cuenta consultada.

---

### Escenario 2 — TC-HUCM02-02: Consulta de cuenta con ID inexistente

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema responde correctamente cuando se consulta una cuenta que no existe         |
| **Técnica**     | Prueba negativa — ID inexistente                                                                    |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | No existe ninguna cuenta con ID "ACC999" en la base de datos                                    |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que no existe ninguna cuenta con ID "ACC999" en el sistema
When el administrador envía GET /cuentas/ACC999
Then el sistema devuelve código de estado 404 Not Found
And la respuesta contiene un mensaje de error descriptivo
```

**Resultado esperado:** El sistema devuelve `404 Not Found` con un mensaje de error descriptivo cuando la cuenta no existe.

---

### Escenario 3 — TC-HUCM02-03: Consulta del listado completo de cuentas

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema devuelve correctamente el listado de todas las cuentas registradas         |
| **Técnica**     | Partición de equivalencia — colección de datos                                                      |
| **Prioridad**   | Media                                                                                               |
| **Precondiciones** | Existen al menos dos cuentas registradas en la base de datos                                    |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existen cuentas registradas en el sistema
When el administrador envía GET /cuentas
Then el sistema devuelve código de estado 200 OK
And la respuesta contiene una colección con todas las cuentas registradas
And cada elemento de la colección contiene los campos de la cuenta
```

**Resultado esperado:** El sistema devuelve `200 OK` con una colección completa de todas las cuentas registradas en la base de datos.

---

### Escenario 4 — TC-HUCM02-04: Actualización exitosa de tipoCuenta y estado

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema actualiza correctamente los campos `tipoCuenta` y `estado` de una cuenta  |
| **Técnica**     | Partición de equivalencia — datos de actualización válidos                                          |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con ID "ACC001" con `tipoCuenta` "AHORRO" y estado activo                     |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta con ID "ACC001" con tipoCuenta "AHORRO"
When el administrador envía PUT /cuentas/ACC001 con tipoCuenta "CORRIENTE" y estado false
Then el sistema actualiza la cuenta exitosamente
And el sistema devuelve código de estado 200 OK
And la respuesta refleja tipoCuenta "CORRIENTE" y estado false
And los cambios quedan persistidos en la base de datos
```

**Resultado esperado:** El sistema devuelve `200 OK` con los datos actualizados. Los campos `tipoCuenta` y `estado` reflejan los nuevos valores en la base de datos.

---

### Escenario 5 — TC-HUCM02-05: Intento de modificación directa del saldo desde el endpoint de gestión

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema impide la modificación directa del campo `saldo` a través de `PUT/PATCH /cuentas/{id}` |
| **Técnica**     | Prueba negativa — campo protegido                                                                   |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con ID "ACC001" y saldo 1000.00                                               |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta con ID "ACC001" y saldo 1000.00
When el administrador envía PUT /cuentas/ACC001 incluyendo el campo saldo con valor 99999.00
Then el sistema ignora el campo saldo o devuelve un error
And el saldo de la cuenta permanece en 1000.00 en la base de datos
And el campo saldo no es modificado por esta operación
```

**Resultado esperado:** El saldo de la cuenta permanece invariable. El sistema ignora el campo `saldo` o devuelve un error, garantizando que solo puede modificarse mediante el registro de movimientos.

---

### Escenario 6 — TC-HUCM02-06: Eliminación lógica de una cuenta

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema realiza una eliminación lógica cambiando el estado a false, sin borrado físico |
| **Técnica**     | Prueba basada en estado — transición activo → inactivo                                              |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con ID "ACC001" con estado activo en la base de datos                         |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — Integración (Backend + Base de datos)                                                |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta con ID "ACC001" con estado activo en el sistema
When el administrador envía DELETE /cuentas/ACC001
Then el sistema actualiza el estado de la cuenta a false
And el sistema devuelve código de estado 204 No Content
And el registro de la cuenta sigue existiendo en la base de datos con estado false
And los movimientos asociados a la cuenta siguen siendo accesibles
```

**Resultado esperado:** La cuenta no es eliminada físicamente. El campo `estado` cambia a `false`. El sistema devuelve `204 No Content` y el historial de movimientos permanece intacto.

---

## HU-CM03: Registro de un movimiento en una cuenta

---

### Escenario 1 — TC-HUCM03-01: Registro exitoso de un depósito

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que un depósito incrementa correctamente el saldo de la cuenta y registra el movimiento   |
| **Técnica**     | Partición de equivalencia — valor positivo válido                                                   |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta activa con numeroCuenta "1234567890" y saldo 500.00                           |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta activa con numeroCuenta "1234567890" y saldo 500.00
When el administrador envía POST /movimientos con numeroCuenta "1234567890" y valor 200.00
Then el sistema registra el movimiento como depósito
And el saldo de la cuenta se actualiza a 700.00
And el sistema devuelve código de estado 201 Created
And la respuesta contiene el detalle del movimiento con fecha, tipo "DEPOSITO", valor 200.00 y saldo resultante 700.00
And el movimiento queda almacenado en la base de datos
```

**Resultado esperado:** El saldo de la cuenta se incrementa en $200.00 quedando en $700.00. El movimiento queda registrado con todos sus campos y el sistema devuelve `201 Created`.

---

### Escenario 2 — TC-HUCM03-02: Registro exitoso de un retiro dentro del saldo disponible

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que un retiro decrementa correctamente el saldo de la cuenta y registra el movimiento     |
| **Técnica**     | Partición de equivalencia — valor negativo válido dentro del saldo                                  |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta activa con numeroCuenta "1234567890" y saldo 500.00                           |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta activa con numeroCuenta "1234567890" y saldo 500.00
When el administrador envía POST /movimientos con numeroCuenta "1234567890" y valor -200.00
Then el sistema registra el movimiento como retiro
And el saldo de la cuenta se actualiza a 300.00
And el sistema devuelve código de estado 201 Created
And la respuesta contiene el detalle del movimiento con tipo "RETIRO", valor -200.00 y saldo resultante 300.00
And el movimiento queda almacenado en la base de datos
```

**Resultado esperado:** El saldo de la cuenta se decrementa en $200.00 quedando en $300.00. El movimiento queda registrado correctamente y el sistema devuelve `201 Created`.

---

### Escenario 3 — TC-HUCM03-03: Retiro rechazado por saldo insuficiente

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza un retiro cuando el valor supera el saldo disponible en la cuenta  |
| **Técnica**     | Análisis de valores límite — valor superior al saldo disponible                                     |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta activa con numeroCuenta "1234567890" y saldo 300.00                           |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta activa con numeroCuenta "1234567890" y saldo 300.00
When el administrador envía POST /movimientos con numeroCuenta "1234567890" y valor -400.00
Then el sistema detecta que el saldo es insuficiente
And el sistema rechaza la transacción
And el sistema devuelve código de estado 400 Bad Request
And la respuesta contiene el mensaje "Saldo no disponible"
And el saldo de la cuenta permanece en 300.00 sin cambios
And no se registra ningún movimiento en la base de datos
```

**Resultado esperado:** El sistema devuelve `400 Bad Request` con el mensaje "Saldo no disponible". El saldo permanece en $300.00 y no se crea ningún registro de movimiento.

---

### Escenario 4 — TC-HUCM03-04: Retiro por el valor exactamente igual al saldo disponible

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar el comportamiento del sistema cuando el valor del retiro es exactamente igual al saldo disponible |
| **Técnica**     | Análisis de valores límite — valor en el límite exacto del saldo                                    |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta activa con numeroCuenta "1234567890" y saldo exactamente 300.00               |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta activa con numeroCuenta "1234567890" y saldo 300.00
When el administrador envía POST /movimientos con numeroCuenta "1234567890" y valor -300.00
Then el sistema procesa el retiro exitosamente
And el saldo de la cuenta se actualiza a 0.00
And el sistema devuelve código de estado 201 Created
And la respuesta contiene el saldo resultante 0.00
```

**Resultado esperado:** El sistema permite el retiro cuando el valor es exactamente igual al saldo disponible. El saldo queda en $0.00 y el movimiento se registra correctamente.

---

### Escenario 5 — TC-HUCM03-05: Movimiento sobre cuenta inexistente

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza un movimiento cuando el `numeroCuenta` no existe                   |
| **Técnica**     | Prueba negativa — referencia inexistente                                                             |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | No existe ninguna cuenta con numeroCuenta "9999999999" en la base de datos                      |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que no existe una cuenta con numeroCuenta "9999999999" en el sistema
When el administrador envía POST /movimientos con numeroCuenta "9999999999" y valor 100.00
Then el sistema no encuentra la cuenta
And el sistema devuelve un código de error (404 Not Found o 400 Bad Request)
And no se registra ningún movimiento en la base de datos
```

**Resultado esperado:** El sistema devuelve un error descriptivo. No se crea ningún registro de movimiento en la base de datos.

---

### Escenario 6 — TC-HUCM03-06: Movimiento sobre cuenta inactiva

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema rechaza movimientos sobre cuentas con estado inactivo                      |
| **Técnica**     | Prueba basada en estado — cuenta inactiva                                                           |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existe una cuenta con numeroCuenta "1234567890" con estado false (inactiva)                     |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existe una cuenta con numeroCuenta "1234567890" con estado inactivo
When el administrador envía POST /movimientos con numeroCuenta "1234567890" y valor 100.00
Then el sistema detecta que la cuenta está inactiva
And el sistema rechaza la transacción
And el sistema devuelve un código de error con mensaje descriptivo
And no se registra ningún movimiento ni se modifica el saldo
```

**Resultado esperado:** El sistema devuelve un error indicando que la cuenta está inactiva. No se procesa ninguna transacción ni se modifica el saldo.

---

## HU-CM04: Generación de reporte de estado de cuenta

---

### Escenario 1 — TC-HUCM04-01: Reporte exitoso con movimientos en el rango de fechas

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema genera correctamente el reporte de estado de cuenta con movimientos dentro del rango especificado y enriquecido con datos del cliente |
| **Técnica**     | Partición de equivalencia — parámetros válidos con datos existentes                                 |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | Existen cuentas y movimientos registrados para el `clienteId` "C001" entre el 01/03/2026 y el 31/03/2026; `ms-customer-service` está activo |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — Integración (REST API + Inter-servicio)                                              |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que existen movimientos para el clienteId "C001" entre el 01/03/2026 y el 31/03/2026
And el servicio de clientes está activo y devuelve el nombre "Juan Pérez" para el clienteId "C001"
When el administrador envía GET /reportes?clienteId=C001&fechaInicio=2026-03-01&fechaFin=2026-03-31
Then el sistema obtiene todas las cuentas del cliente "C001"
And el sistema filtra los movimientos dentro del rango de fechas
And el sistema obtiene el nombre del cliente desde ms-customer-service
And el sistema devuelve código de estado 200 OK
And la respuesta contiene una lista de movimientos enriquecida con datos del cliente "Juan Pérez" y de cada cuenta
And solo se incluyen movimientos dentro del rango 01/03/2026 — 31/03/2026
```

**Resultado esperado:** El sistema devuelve `200 OK` con el reporte completo, incluyendo todos los movimientos del período filtrados correctamente y enriquecidos con el nombre del cliente.

---

### Escenario 2 — TC-HUCM04-02: Reporte con rango de fechas sin movimientos

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema devuelve una respuesta válida cuando no existen movimientos en el rango de fechas especificado |
| **Técnica**     | Prueba negativa — rango vacío                                                                       |
| **Prioridad**   | Media                                                                                               |
| **Precondiciones** | El `clienteId` "C001" tiene cuentas registradas pero no tiene movimientos entre el 01/01/2020 y el 31/01/2020 |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que el clienteId "C001" no tiene movimientos entre el 01/01/2020 y el 31/01/2020
When el administrador envía GET /reportes?clienteId=C001&fechaInicio=2020-01-01&fechaFin=2020-01-31
Then el sistema devuelve código de estado 200 OK
And la respuesta contiene una colección vacía de movimientos
And no se genera ningún error ni excepción
```

**Resultado esperado:** El sistema devuelve `200 OK` con una colección vacía cuando no hay movimientos en el período especificado, sin generar errores.

---

### Escenario 3 — TC-HUCM04-03: Reporte con clienteId inexistente

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema responde adecuadamente cuando el `clienteId` no tiene cuentas asociadas o no existe |
| **Técnica**     | Prueba negativa — clienteId sin cuentas                                                             |
| **Prioridad**   | Media                                                                                               |
| **Precondiciones** | El `clienteId` "C999" no tiene ninguna cuenta asociada en `ms-account-service`                  |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que el clienteId "C999" no tiene cuentas asociadas en el sistema
When el administrador envía GET /reportes?clienteId=C999&fechaInicio=2026-01-01&fechaFin=2026-03-31
Then el sistema devuelve una respuesta indicando que no hay datos disponibles
And no se genera ninguna excepción no controlada
```

**Resultado esperado:** El sistema devuelve una respuesta controlada indicando que no existen cuentas o movimientos para el cliente especificado, sin generar errores inesperados.

---

### Escenario 4 — TC-HUCM04-04: Reporte excluye movimientos fuera del rango de fechas

| Campo           | Detalle                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------- |
| **Propósito**   | Verificar que el sistema filtra correctamente los movimientos y no incluye registros fuera del rango de fechas especificado |
| **Técnica**     | Análisis de valores límite — fechas en los límites del rango                                        |
| **Prioridad**   | Alta                                                                                                |
| **Precondiciones** | La cuenta del `clienteId` "C001" tiene movimientos registrados en febrero, marzo y abril de 2026 |
| **Proyecto**    | Plataforma-Bancaria-Account-Service                                                                 |
| **Tipo de prueba** | Funcional — REST API (Backend)                                                                   |
| **Implementado** | Sí                                                                                                 |
| **Estado**      | Cerrado                                                                                             |

```
Given que el clienteId "C001" tiene movimientos en febrero, marzo y abril de 2026
When el administrador envía GET /reportes?clienteId=C001&fechaInicio=2026-03-01&fechaFin=2026-03-31
Then el sistema devuelve código 200 OK
And la respuesta solo contiene movimientos del mes de marzo de 2026
And no se incluyen movimientos de febrero ni de abril de 2026
```

**Resultado esperado:** El reporte incluye únicamente los movimientos dentro del rango especificado. Los movimientos de fechas anteriores o posteriores no aparecen en la respuesta.
