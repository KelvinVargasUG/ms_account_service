### **Microservicio 2: Servicio de Cuentas y Movimientos**

Este microservicio gestionará las entidades `Cuenta` y `Movimiento`, incluyendo la lógica de negocio transaccional.

#### **Historias de Usuario Funcionales (F1, F2, F3, F4)**

**HU-CM01: Creación de una nueva cuenta bancaria**

- **Como** administrador del sistema,
- **Quiero** crear una nueva cuenta bancaria y asociarla a un cliente existente,
- **Para** permitir que los clientes gestionen su dinero.

  **Criterios de Aceptación:**
  1.  Se debe poder enviar una solicitud POST al endpoint `/cuentas`.
  2.  La solicitud debe incluir `numeroCuenta`, `tipoCuenta`, `saldoInicial`, `estado` y el `clienteId` al que pertenece.
  3.  El sistema debe validar que el `clienteId` corresponda a un cliente activo y existente (esto puede requerir una comunicación sincrónica o asincrónica con el Servicio de Clientes).
  4.  El `numeroCuenta` debe ser único.
  5.  Si la creación es exitosa, se devuelve un `201 Created` con los datos de la cuenta creada.
  6.  Si el `clienteId` no es válido, se devuelve un `400 Bad Request`.

**HU-CM02: Gestión de cuentas (CRUD)**

- **Como** administrador del sistema,
- **Quiero** consultar, actualizar y eliminar cuentas bancarias,
- **Para** administrar los productos financieros de los clientes.

  **Criterios de Aceptación:**
  1.  **Consultar:** GET a `/cuentas` y `/cuentas/{id}` debe devolver la información de las cuentas.
  2.  **Actualizar:** PUT/PATCH a `/cuentas/{id}` debe permitir modificar `tipoCuenta` y `estado`. No se debe permitir modificar el saldo directamente por este endpoint.
  3.  **Eliminar:** DELETE a `/cuentas/{id}` debe realizar una eliminación lógica (cambiar `estado` a `false`).

**HU-CM03: Registro de un movimiento en una cuenta**

- **Como** cliente o administrador,
- **Quiero** registrar un movimiento de depósito o retiro en una cuenta específica,
- **Para** afectar el saldo y mantener un registro de las transacciones.

  **Criterios de Aceptación:**
  1.  Se debe poder enviar una solicitud POST al endpoint `/movimientos`.
  2.  La solicitud debe incluir el `numeroCuenta`, `tipoMovimiento` (o inferirlo del valor) y el `valor` de la transacción.
  3.  Si el `valor` es positivo, se considera un depósito y se suma al saldo de la cuenta.
  4.  Si el `valor` es negativo, se considera un retiro y se resta del saldo de la cuenta.
  5.  El sistema debe verificar que la cuenta exista y esté activa.
  6.  **(F3)** Si es un retiro y el saldo disponible es insuficiente, el sistema debe rechazar la transacción y devolver un código `400 Bad Request` con el mensaje "Saldo no disponible".
  7.  **(F2)** Si la transacción es exitosa, se debe actualizar el `saldo` en la entidad `Cuenta` y crear un nuevo registro en la entidad `Movimientos` con la fecha, tipo, valor y el saldo resultante.
  8.  Se debe devolver un `201 Created` con el detalle del movimiento registrado.

**HU-CM04: Generación de reporte de estado de cuenta**

- **Como** cliente o administrador,
- **Quiero** generar un reporte de estado de cuenta para un cliente en un rango de fechas específico,
- **Para** auditar los movimientos y saldos de sus cuentas.

  **Criterios de Aceptación:**
  1.  Se debe poder enviar una solicitud GET al endpoint `/reportes`.
  2.  La solicitud debe aceptar como parámetros de consulta (`query params`) el `clienteId` y un rango de fechas (ej. `fechaInicio` y `fechaFin`).
  3.  El servicio debe obtener todas las cuentas asociadas al `clienteId`.
  4.  Para cada cuenta, debe obtener todos los movimientos realizados dentro del rango de fechas especificado.
  5.  El servicio debe devolver un código `200 OK` con una respuesta en formato JSON que contenga una lista de movimientos, enriquecida con los datos del cliente y la cuenta, tal como se especifica en el ejemplo.
  6.  La información del cliente (ej. "Marianela Montalvo") deberá ser obtenida del Servicio de Clientes mediante comunicación inter-servicio.


#### **Historias de Usuario Tecnicas (F1, F2, F3, F4)**

Estas historias se centran en la integridad transaccional, la resiliencia y el rendimiento del microservicio más crítico.

**HU-T-CM01: Implementación de pruebas de integración**
*   **Como** desarrollador,
*   **Quiero** crear pruebas de integración para el flujo de creación de movimientos,
*   **Para** verificar que la interacción entre el controlador, el servicio y la capa de persistencia funciona correctamente, incluyendo la lógica transaccional.

    **Criterios de Aceptación:**
    1.  La prueba debe utilizar una base de datos de prueba (en memoria o en un contenedor Docker).
    2.  Se debe probar el escenario de "saldo insuficiente" (F3) y verificar que la transacción se revierta (rollback).
    3.  Se debe probar un escenario exitoso y validar que tanto el saldo de la cuenta como el registro del movimiento se persistan correctamente.

**HU-T-CM02: Garantizar atomicidad en transacciones**
*   **Como** desarrollador,
*   **Quiero** asegurar que la creación de un movimiento y la actualización del saldo de la cuenta se ejecuten dentro de una única transacción atómica,
*   **Para** mantener la consistencia e integridad de los datos financieros, evitando estados inconsistentes en caso de fallo.

    **Criterios de Aceptación:**
    1.  El método de servicio que orquesta la operación debe estar anotado con `@Transactional` (o su equivalente en el framework utilizado).
    2.  Si ocurre un error después de crear el movimiento pero antes de actualizar el saldo (o viceversa), toda la operación debe ser revertida.

**HU-T-CM03: Implementación de comunicación resiliente inter-servicio**
*   **Como** desarrollador,
*   **Quiero** implementar un patrón de resiliencia (como Circuit Breaker) para las llamadas síncronas al Servicio de Clientes durante la generación de reportes,
*   **Para** evitar que una falla o latencia en el Servicio de Clientes cause una falla en cascada en el Servicio de Cuentas.

    **Criterios de Aceptación:**
    1.  Se debe utilizar una librería como Resilience4j o Hystrix.
    2.  Si el Servicio de Clientes no responde o devuelve errores repetidamente, el circuito debe "abrirse" y fallar rápidamente.
    3.  Se debe definir una lógica de fallback (ej. devolver el reporte con un nombre de cliente genérico o un ID) cuando el circuito esté abierto.

**HU-T-CM04: Optimización de consultas de reportes**
*   **Como** desarrollador,
*   **Quiero** crear los índices de base de datos necesarios en la tabla de `Movimientos`,
*   **Para** optimizar el rendimiento de las consultas de reportes que filtran por cliente y rango de fechas.

    **Criterios de Aceptación:**
    1.  Se debe crear un índice compuesto en las columnas utilizadas para filtrar los reportes (ej. `cuenta_id` y `fecha`).
    2.  Se debe verificar la mejora en el tiempo de respuesta de la consulta a través de un `EXPLAIN ANALYZE` o una herramienta similar.