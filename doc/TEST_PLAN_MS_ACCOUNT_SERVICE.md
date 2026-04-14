# Plan de Pruebas — Account & Movement Service Management

| Campo    | Detalle                                                   |
| -------- | --------------------------------------------------------- |
| Versión  | 1.0                                                       |
| Fecha    | 13/04/2026                                                |
| Estado   | En revisión                                               |
| Proyecto | Plataforma Bancaria — Microservicio de Cuentas y Movimientos |
| Área     | QA                                                        |

---

# 1. Introducción

## 1.1 Propósito

Este documento define la estrategia, alcance y criterios de calidad para validar el **Microservicio de Cuentas y Movimientos (ms-account-service)**, el cual gestiona las entidades `Cuenta` y `Movimiento` dentro de la plataforma bancaria, incluyendo la lógica de negocio transaccional, la generación de reportes y la comunicación inter-servicio con el Microservicio de Clientes.

Su objetivo es garantizar la correcta funcionalidad del sistema, la integridad transaccional de los datos financieros, la resiliencia ante fallos de servicios dependientes y el cumplimiento de los requisitos funcionales y técnicos definidos en las historias de usuario.

---

## 1.2 Contexto

El microservicio permite:

- Creación y gestión de cuentas bancarias asociadas a clientes activos (CRUD completo)
- Registro de movimientos de depósito y retiro con actualización atómica del saldo
- Validación de saldo disponible para rechazar retiros insuficientes
- Generación de reportes de estado de cuenta por cliente y rango de fechas
- Comunicación resiliente inter-servicio con `ms-customer-service` (Circuit Breaker)
- Optimización de consultas mediante índices de base de datos

Esta funcionalidad impacta directamente en:

- API REST expuesta en los endpoints `/cuentas`, `/movimientos` y `/reportes`
- Base de datos relacional (entidades `Cuenta` y `Movimiento`)
- Integración sincrónica con `ms-customer-service` para validación de clientes y enriquecimiento de reportes
- Integridad transaccional (atomicidad en operaciones financieras)
- Rendimiento de consultas sobre grandes volúmenes de movimientos

---

# 2. Alcance de las Pruebas

## 2.1 Funcionalidades en Alcance

| Épica                                              | Historia de Usuario | Descripción                                                   | Prioridad |
| -------------------------------------------------- | ------------------- | ------------------------------------------------------------- | --------- |
| **EPIC-01 — Gestión de Cuentas (CRUD)**            | HU-CM01             | Creación de una nueva cuenta bancaria                         | Alta      |
|                                                    | HU-CM02             | Consulta, actualización y eliminación lógica de cuentas       | Alta      |
| **EPIC-02 — Movimientos y Transacciones**          | HU-CM03             | Registro de movimiento (depósito/retiro) con actualización de saldo | Alta |
| **EPIC-03 — Reportes**                             | HU-CM04             | Generación de reporte de estado de cuenta por rango de fechas | Alta      |
| **EPIC-04 — Calidad y Resiliencia Técnica**        | HU-T-CM01           | Pruebas de integración del flujo de movimientos               | Alta      |
|                                                    | HU-T-CM02           | Atomicidad en transacciones (`@Transactional`)                | Alta      |
|                                                    | HU-T-CM03           | Circuit Breaker para comunicación con `ms-customer-service`   | Alta      |
|                                                    | HU-T-CM04           | Optimización de consultas de reportes mediante índices        | Media     |

---

## 2.2 Fuera de Alcance

- Autenticación y autorización de usuarios (JWT/OAuth2)
- Gestión del ciclo de vida de clientes (`ms-customer-service`)
- Procesamiento de pagos externos o integraciones con sistemas bancarios legacy
- Módulo de notificaciones
- Frontend de administración
- Auditoría avanzada de seguridad

---

## 2.3 Tipos de Prueba

Se contemplan los siguientes tipos de pruebas para garantizar la calidad funcional y técnica del microservicio:

- **Pruebas funcionales:**
  Orientadas a validar el comportamiento del sistema a nivel de historias de usuario: creación de cuentas, registro de movimientos, control de saldo insuficiente, operaciones CRUD y generación de reportes.

- **Pruebas de integración:**
  Verifican la interacción correcta entre la capa de servicio, el repositorio y la base de datos. Incluyen la validación de la atomicidad transaccional (rollback ante fallos) y la comunicación con `ms-customer-service`.

- **Pruebas de resiliencia:**
  Evalúan el comportamiento del sistema ante fallos o latencia en el Servicio de Clientes, verificando la activación del Circuit Breaker y la ejecución de la lógica de fallback.

- **Pruebas End-to-End (E2E):**
  Validan los flujos completos desde la perspectiva del consumidor de la API, incluyendo el ciclo de vida de una cuenta y el flujo de depósito/retiro con generación de reporte.

- **Pruebas no funcionales:**
  Evalúan el rendimiento de las consultas de reportes, la correcta aplicación de índices de base de datos y los tiempos de respuesta bajo diferentes volúmenes de datos.

---

# 3. Objetivos de Calidad

| Criterio                              | Descripción                                                                                                                                                                                     |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Cobertura funcional**               | Garantizar la validación del 100% de los criterios de aceptación definidos para las historias HU-CM01 a HU-CM04, cubriendo flujos principales, alternativos y escenarios de error.             |
| **Integridad transaccional**          | Asegurar que la creación de un movimiento y la actualización del saldo de la cuenta se ejecuten de forma atómica, garantizando rollback completo ante cualquier fallo durante la operación.     |
| **Integridad de datos financieros**   | Verificar que el saldo de la cuenta se actualice correctamente en cada transacción y que el historial de movimientos refleje fielmente todas las operaciones realizadas.                         |
| **Control de saldo insuficiente**     | Confirmar que el sistema rechaza con `400 Bad Request` y el mensaje "Saldo no disponible" todo intento de retiro que supere el saldo disponible en la cuenta.                                   |
| **Unicidad de número de cuenta**      | Verificar que el sistema impide la creación de cuentas con un `numeroCuenta` ya existente, devolviendo el error correspondiente.                                                                |
| **Resiliencia inter-servicio**        | Validar que el Circuit Breaker se activa correctamente ante fallos del Servicio de Clientes, que el circuito se abre tras errores repetidos y que la lógica de fallback garantiza la continuidad del servicio. |
| **Rendimiento en reportes**           | Confirmar que los índices de base de datos en la tabla `Movimientos` reducen los tiempos de respuesta de las consultas de reportes por cliente y rango de fechas a niveles aceptables.          |
| **Consistencia del estado de cuentas**| Verificar que las eliminaciones son estrictamente lógicas (`estado = false`) y que el campo `saldo` no sea modificable directamente desde el endpoint de gestión de cuentas.                    |

---

# 4. Estrategia de Pruebas

## 4.1 Enfoque General

La estrategia de pruebas se basa en un enfoque progresivo, iniciando con la validación de la lógica de negocio en la capa de servicio y escalando hacia la verificación de la integración entre componentes, la atomicidad transaccional y la resiliencia ante fallos externos.

Se prioriza la validación de los escenarios críticos del negocio — operaciones de movimientos, integridad del saldo y atomicidad transaccional — antes de abordar escenarios alternativos, condiciones de error y aspectos de calidad técnica (resiliencia, optimización de consultas).

---

## 4.2 Niveles de Prueba

| Nivel       | Tipo de Prueba                   | Descripción                                                                                                                                                                                                                                                                 |
| ----------- | -------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Nivel 1** | **Pruebas Unitarias**            | Validan la lógica de negocio en la capa de servicio de forma aislada, usando mocks para el repositorio y el cliente HTTP de `ms-customer-service`. Cubren cálculo de saldo, validación de saldo insuficiente y reglas de negocio en creación de cuentas y movimientos.     |
| **Nivel 2** | **Pruebas Funcionales**          | Validan el comportamiento del sistema a nivel de historias de usuario, verificando los criterios de aceptación mediante llamadas a los endpoints REST. Incluye validación de códigos HTTP, estructura del JSON de respuesta y reglas de negocio.                            |
| **Nivel 3** | **Pruebas de Integración**       | Verifican la interacción entre la capa de servicio, el repositorio y la base de datos (en memoria o contenedor Docker). Validan atomicidad transaccional (rollback), persistencia correcta del saldo y comunicación con `ms-customer-service`.                              |
| **Nivel 4** | **Pruebas de Resiliencia**       | Simulan fallos y latencia en `ms-customer-service` para verificar la activación del Circuit Breaker (Resilience4j), la apertura del circuito tras errores repetidos y la correcta ejecución de la lógica de fallback en la generación de reportes.                         |
| **Nivel 5** | **Pruebas End-to-End (E2E)**     | Evalúan los flujos completos desde la perspectiva del consumidor de la API, incluyendo el ciclo de vida completo de una cuenta y el flujo de depósito/retiro con generación de reporte de estado de cuenta.                                                                 |
| **Nivel 6** | **Pruebas No Funcionales**       | Analizan el rendimiento de las consultas de reportes, verificando la correcta aplicación de índices mediante `EXPLAIN ANALYZE`, y evalúan tiempos de respuesta bajo diferentes volúmenes de movimientos.                                                                    |

---

## 4.3 Técnicas de Prueba

Para garantizar una cobertura adecuada, se aplican las siguientes técnicas de diseño de pruebas:

- **Partición de equivalencia:** para validar conjuntos de datos válidos e inválidos (ej. `clienteId` existente vs inexistente, valor positivo vs negativo vs cero).
- **Análisis de valores límite:** aplicado a campos como `valor` del movimiento (exactamente igual al saldo disponible, un centavo más del saldo disponible) y rangos de fechas para reportes.
- **Pruebas negativas:** orientadas a verificar el comportamiento ante entradas incorrectas, saldo insuficiente, cuentas inactivas, `numeroCuenta` duplicados y `clienteId` inválidos.
- **Pruebas basadas en estado:** para validar las transiciones del campo `estado` de la cuenta (activa/inactiva) y su impacto en la aceptación de nuevos movimientos.
- **Pruebas de inyección de fallos:** para validar el comportamiento del Circuit Breaker simulando la indisponibilidad del Servicio de Clientes.
- **Pruebas de atomicidad:** para verificar que los datos financieros permanecen consistentes ante fallos durante la ejecución de una transacción.

---

# 5. Cobertura de Requerimientos

| Historia de Usuario | Descripción                                         | Tipos de Prueba Aplicados                     | Enfoque de Validación                                                                                                                                                |
| ------------------- | --------------------------------------------------- | --------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **HU-CM01**         | Creación de cuenta bancaria                         | Funcional, Integración                        | Validación de creación exitosa (201), unicidad de `numeroCuenta`, validación de `clienteId` activo contra `ms-customer-service` y error por `clienteId` inválido (400). |
| **HU-CM02**         | Gestión CRUD de cuentas                             | Funcional, Integración                        | Consulta individual y listado (200/404), actualización de `tipoCuenta` y `estado`, intento de modificación directa del saldo (error esperado) y eliminación lógica. |
| **HU-CM03**         | Registro de movimiento (depósito/retiro)            | Funcional, Integración, Unitaria              | Depósito exitoso (saldo incrementado), retiro exitoso (saldo decrementado), retiro con saldo insuficiente (400 "Saldo no disponible"), validación de cuenta activa y persistencia de movimiento con saldo resultante. |
| **HU-CM04**         | Reporte de estado de cuenta por rango de fechas     | Funcional, Integración, No funcional          | Reporte con datos válidos (200), filtrado correcto por `clienteId` y rango de fechas, enriquecimiento con datos del cliente desde `ms-customer-service` y tiempos de respuesta aceptables. |
| **HU-T-CM01**       | Pruebas de integración del flujo de movimientos     | Integración                                   | Ejecución con base de datos en memoria/contenedor, verificación de rollback ante saldo insuficiente y persistencia correcta de saldo + movimiento en escenario exitoso. |
| **HU-T-CM02**       | Atomicidad transaccional (`@Transactional`)         | Integración, Unitaria                         | Verificación de rollback completo cuando ocurre un error entre la creación del movimiento y la actualización del saldo. Confirmación de que ninguna operación parcial persiste. |
| **HU-T-CM03**       | Circuit Breaker para comunicación inter-servicio    | Resiliencia, Integración                      | Activación del Circuit Breaker ante fallos del Servicio de Clientes, apertura del circuito tras errores repetidos, ejecución del fallback y recuperación del circuito tras restauración del servicio. |
| **HU-T-CM04**       | Optimización de consultas mediante índices          | No funcional                                  | Verificación de la existencia del índice compuesto en `cuenta_id` y `fecha`, mejora medible en tiempo de respuesta mediante `EXPLAIN ANALYZE` con y sin índice.    |

---

# 6. Flujos E2E

## Flujo E2E-01 — Creación de cuenta y primer depósito

| Campo                  | Detalle                                                                                                                                                                                                                                                                                                                                                 |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | Existe un cliente activo en `ms-customer-service`. El microservicio `ms-account-service` está desplegado y la base de datos operativa. No existe ninguna cuenta con el `numeroCuenta` a registrar.                                                                                                                                                      |
| **Pasos**              | 1. El administrador envía POST a `/cuentas` con `numeroCuenta`, `tipoCuenta`, `saldoInicial`, `estado` y `clienteId` válido.<br>2. El sistema valida que el `clienteId` corresponda a un cliente activo.<br>3. El sistema verifica que el `numeroCuenta` sea único.<br>4. El sistema persiste la cuenta y devuelve `201 Created`.<br>5. El administrador envía POST a `/movimientos` con `numeroCuenta` y `valor` positivo.<br>6. El sistema incrementa el saldo y registra el movimiento.<br>7. El sistema devuelve `201 Created` con el detalle del movimiento y el saldo resultante. |
| **Resultado Esperado** | La cuenta es creada exitosamente. El depósito incrementa el saldo correctamente. El movimiento queda registrado con la fecha, tipo, valor y saldo resultante.                                                                                                                                                                                           |

---

## Flujo E2E-02 — Retiro exitoso y retiro con saldo insuficiente

| Campo                  | Detalle                                                                                                                                                                                                                                                                                                                                                                                                   |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | Existe una cuenta activa con saldo conocido (ej. $500.00).                                                                                                                                                                                                                                                                                                                                                |
| **Pasos**              | 1. El administrador envía POST a `/movimientos` con `valor` negativo dentro del saldo disponible (ej. -$200.00).<br>2. El sistema verifica el saldo disponible, lo decrementa y registra el movimiento.<br>3. El sistema devuelve `201 Created` (saldo resultante: $300.00).<br>4. El administrador envía POST a `/movimientos` con `valor` negativo que supera el saldo disponible (ej. -$400.00).<br>5. El sistema detecta saldo insuficiente.<br>6. El sistema rechaza la transacción con `400 Bad Request` y el mensaje "Saldo no disponible". |
| **Resultado Esperado** | El primer retiro se procesa correctamente y el saldo queda en $300.00. El segundo retiro es rechazado. El saldo permanece en $300.00 sin ningún movimiento adicional registrado.                                                                                                                                                                                                                           |

---

## Flujo E2E-03 — Generación de reporte de estado de cuenta

| Campo                  | Detalle                                                                                                                                                                                                                                                                                                                                           |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | Existen cuentas y movimientos registrados para un `clienteId` conocido dentro de un rango de fechas específico. El Servicio de Clientes está activo y responde correctamente.                                                                                                                                                                     |
| **Pasos**              | 1. El administrador envía GET a `/reportes?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}`.<br>2. El sistema obtiene todas las cuentas asociadas al `clienteId`.<br>3. El sistema filtra los movimientos de cada cuenta dentro del rango de fechas.<br>4. El sistema consulta el Servicio de Clientes para obtener el nombre del cliente.<br>5. El sistema construye la respuesta enriquecida y devuelve `200 OK`. |
| **Resultado Esperado** | El reporte incluye correctamente todos los movimientos del período, enriquecidos con los datos del cliente y la información de cada cuenta. Solo se incluyen movimientos dentro del rango de fechas especificado.                                                                                                                                   |

---

## Flujo E2E-04 — Reporte con Circuit Breaker activo (fallback)

| Campo                  | Detalle                                                                                                                                                                                                                                                                                                                         |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | `ms-customer-service` está inaccesible o devolviendo errores. El Circuit Breaker está configurado con Resilience4j.                                                                                                                                                                                                              |
| **Pasos**              | 1. El administrador envía GET a `/reportes?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}`.<br>2. El sistema intenta comunicarse con el Servicio de Clientes para obtener los datos del cliente.<br>3. El Servicio de Clientes no responde o devuelve errores repetidamente.<br>4. El Circuit Breaker se abre.<br>5. El sistema ejecuta la lógica de fallback y devuelve el reporte con el `clienteId` en lugar del nombre del cliente. |
| **Resultado Esperado** | El reporte es generado sin bloqueos. Los datos de movimientos y cuentas son correctos. El nombre del cliente muestra el valor de fallback configurado. No se genera ninguna excepción no controlada.                                                                                                                              |

---

## Flujo E2E-05 — Ciclo de vida completo de una cuenta

| Campo                  | Detalle                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | El microservicio está desplegado con base de datos operativa y un cliente activo disponible en `ms-customer-service`.                                                                                                                                                                                                                                                                                                                |
| **Pasos**              | 1. Crear cuenta → POST `/cuentas` → `201 Created`.<br>2. Consultar cuenta → GET `/cuentas/{id}` → `200 OK`.<br>3. Registrar depósito → POST `/movimientos` (valor positivo) → `201 Created`.<br>4. Registrar retiro → POST `/movimientos` (valor negativo dentro del saldo) → `201 Created`.<br>5. Consultar listado de cuentas → GET `/cuentas` → `200 OK`.<br>6. Actualizar `tipoCuenta` → PUT `/cuentas/{id}` → `200 OK`.<br>7. Generar reporte → GET `/reportes?clienteId=...` → `200 OK`.<br>8. Eliminar lógicamente cuenta → DELETE `/cuentas/{id}` → `204 No Content`. |
| **Resultado Esperado** | Todas las operaciones se ejecutan correctamente en secuencia. El saldo refleja exactamente cada operación. El registro nunca es eliminado físicamente. El reporte incluye todos los movimientos del período.                                                                                                                                                                                                                          |

---

# 7. Criterios de Entrada y Salida

## 7.1 Criterios de Entrada

| ID    | Criterio                                                                                                              |
| ----- | --------------------------------------------------------------------------------------------------------------------- |
| CE-01 | El ambiente de pruebas debe estar desplegado y operativo (`ms-account-service` + base de datos).                      |
| CE-02 | El microservicio `ms-customer-service` debe estar disponible o tener un mock configurado para pruebas de integración. |
| CE-03 | La base de datos debe estar configurada, accesible y con el esquema actualizado (incluyendo índices).                 |
| CE-04 | Los datos de prueba mínimos deben estar disponibles (cliente activo, cuentas con saldo conocido).                     |
| CE-05 | Los endpoints de la API deben estar disponibles y responder a solicitudes básicas.                                    |
| CE-06 | La versión de la funcionalidad debe haber sido entregada formalmente al equipo de QA.                                 |
| CE-07 | El Circuit Breaker debe estar configurado y ser verificable en el ambiente de pruebas.                                |

## 7.2 Criterios de Salida

| ID    | Criterio                                                                                                                       |
| ----- | ------------------------------------------------------------------------------------------------------------------------------ |
| CS-01 | El 100% de los casos de prueba de prioridad alta debe haber sido ejecutado.                                                    |
| CS-02 | No deben existir defectos críticos o altos abiertos sin resolución.                                                            |
| CS-03 | Los flujos E2E del ciclo de vida de una cuenta y del flujo transaccional deben haber sido validados de extremo a extremo.      |
| CS-04 | El rollback transaccional debe haber sido validado en al menos un escenario de fallo (saldo insuficiente y fallo inesperado).  |
| CS-05 | El comportamiento del Circuit Breaker y la lógica de fallback deben haber sido validados.                                      |
| CS-06 | La optimización de consultas debe haber sido verificada con `EXPLAIN ANALYZE` o herramienta equivalente.                       |
| CS-07 | El reporte final de pruebas debe estar elaborado y revisado.                                                                   |

---

# 8. Ambiente de Pruebas y Configuración

## 8.1 Configuración del Ambiente

El ambiente de pruebas estará configurado para simular condiciones reales de operación del microservicio, incluyendo la integración con `ms-customer-service` y la validación de escenarios de fallo.

| Componente                         | Descripción                                                                                                                                                        | Responsable  |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------ |
| **ms-account-service**             | Microservicio Spring Boot encargado de la lógica de cuentas, movimientos y reportes, expuesto en `/cuentas`, `/movimientos` y `/reportes`.                        | Dev          |
| **ms-customer-service / Mock**     | Microservicio de clientes real o mock configurado para simular respuestas exitosas y fallos en pruebas de resiliencia.                                             | Dev / QA     |
| **Base de datos relacional**       | Sistema de almacenamiento persistente para las entidades `Cuenta` y `Movimiento`, con índices compuestos en la tabla `Movimientos`.                                | Dev / DBA    |
| **Base de datos en memoria (H2)**  | Utilizada en pruebas de integración de la capa de servicio para verificar atomicidad y rollback sin dependencia del ambiente externo.                              | Dev          |
| **Resilience4j (Circuit Breaker**) | Configurado para gestionar fallos en las llamadas síncronas a `ms-customer-service` durante la generación de reportes.                                             | Dev          |
| **Docker / docker-compose**        | Infraestructura de contenerización para levantar la solución completa en entorno estandarizado.                                                                    | Dev / DevOps |
| **Herramientas de prueba (QA)**    | Herramientas utilizadas para ejecutar pruebas manuales y automatizadas (ej. Postman, JUnit, Mockito, Testcontainers).                                              | QA           |

---

## 8.2 Datos de Prueba

Para la ejecución de las pruebas se deben considerar los siguientes datos:

- Cuentas activas e inactivas con saldos conocidos (ej. $0.00, $500.00, $1000.00).
- `clienteId` de clientes activos e inactivos/inexistentes para validación de unicidad y restricciones.
- `numeroCuenta` únicos y duplicados para verificar el control de unicidad.
- Valores de movimiento: positivos (depósito), negativos (retiro dentro del saldo), negativos que superen el saldo y el valor exactamente igual al saldo disponible.
- Rangos de fechas válidos, vacíos (sin movimientos) y con gran volumen de registros.
- Configuración del Circuit Breaker con un número bajo de intentos para facilitar la activación en pruebas.

---

# 9. Riesgos y Mitigaciones

| ID       | Riesgo                                                                                                                              | Probabilidad | Impacto  | Mitigación                                                                                                                                                          |
| -------- | ----------------------------------------------------------------------------------------------------------------------------------- | :----------: | :------: | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R-01** | Operación parcial persistida en base de datos por fallo en la atomicidad transaccional (saldo actualizado sin movimiento o viceversa). |    Media    | Crítico  | Verificar la anotación `@Transactional` en el método de servicio y crear pruebas de integración que simulen un fallo entre ambas operaciones y confirmen el rollback. |
| **R-02** | Saldo negativo resultante de una condición de carrera concurrente durante retiros simultáneos sobre la misma cuenta.                |     Alta    |  Crítico | Implementar pruebas de concurrencia que ejecuten retiros simultáneos y verificar que el saldo nunca quede en valores negativos. Validar el uso de bloqueo pesimista o `@Version`. |
| **R-03** | Fallo en cascada de `ms-account-service` causado por indisponibilidad o latencia de `ms-customer-service`.                         |    Media    |  Alto    | Verificar la correcta configuración y activación del Circuit Breaker (Resilience4j) y que la lógica de fallback devuelva una respuesta degradada pero válida.       |
| **R-04** | Registros de movimientos con saldo resultante incorrecto por errores en el cálculo del saldo acumulado.                             |    Media    |  Crítico | Diseñar casos de prueba con secuencias de múltiples depósitos y retiros y verificar matemáticamente el saldo resultante en cada operación.                          |
| **R-05** | Inestabilidad del ambiente de pruebas (base de datos inaccesible, contenedores caídos) que bloquee la ejecución de las pruebas.     |    Baja     |  Alto    | Validar el estado del ambiente antes de iniciar pruebas y coordinar con DevOps para asegurar la disponibilidad mediante health checks automatizados.                |
| **R-06** | Degradación del rendimiento en consultas de reportes sobre grandes volúmenes de movimientos sin índices optimizados.                |    Alta     |  Alto    | Verificar la existencia y eficacia del índice compuesto (`cuenta_id`, `fecha`) con `EXPLAIN ANALYZE` y definir umbrales de tiempo de respuesta aceptables.         |
| **R-07** | Modificación accidental del saldo de una cuenta a través del endpoint de gestión de cuentas (`PUT/PATCH /cuentas/{id}`).            |    Media    |  Crítico | Crear prueba que intente modificar el campo `saldo` directamente y verificar que el sistema lo ignora o devuelve un error.                                          |
| **R-08** | Eliminación física de cuentas en lugar de eliminación lógica, causando inconsistencia en el historial de movimientos.               |    Baja     |  Alto    | Verificar tras cada DELETE que el registro persiste en la base de datos con `estado = false` y que los movimientos asociados siguen accesibles.                     |

---

# 10. Cronograma

| Fase                                     | Actividades                                                                                                                                     | Duración Estimada | Responsable  |
| ---------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | :---------------: | ------------ |
| **Fase 1 — Preparación**                 | Revisión de criterios de aceptación, diseño de casos de prueba, configuración del ambiente y preparación de datos de prueba.                    |       2 días      | QA Lead      |
| **Fase 2 — Ejecución Funcional**         | Pruebas sobre los endpoints CRUD de cuentas (`/cuentas`) y registro de movimientos (`/movimientos`), incluyendo retiro con saldo insuficiente.  |       3 días      | QA           |
| **Fase 3 — Pruebas de Integración**      | Validación de atomicidad transaccional (rollback), persistencia correcta del saldo y movimiento, y comunicación con `ms-customer-service`.      |       2 días      | QA + Dev     |
| **Fase 4 — Pruebas de Resiliencia**      | Verificación del Circuit Breaker, activación del fallback y recuperación del circuito ante restauración del Servicio de Clientes.               |       2 días      | QA + Dev     |
| **Fase 5 — Reportes y Rendimiento**      | Validación de la generación de reportes por rango de fechas, enriquecimiento con datos del cliente y rendimiento de consultas con índices.      |       2 días      | QA + Dev     |
| **Fase 6 — Pruebas E2E**                 | Ejecución de los flujos E2E completos: ciclo de vida de cuenta, flujo transaccional, reporte con servicio activo y con Circuit Breaker activo.  |       1 día       | QA           |
| **Fase 7 — Cierre y Reporte**            | Retesting de defectos, validación final y generación del reporte de pruebas.                                                                    |       1 día       | QA           |

---

# 11. Roles

| Rol                     | Perfil sugerido        | Responsabilidades                                                                                                                                                                                   |
| ----------------------- | ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **QA Lead**             | Analista QA Senior     | Definir y mantener el plan de pruebas. Diseñar la estrategia de testing. Supervisar la ejecución de pruebas. Priorizar defectos y coordinar con el equipo. Elaborar el reporte final de calidad.    |
| **QA**                  | Analista QA            | Diseñar y ejecutar casos de prueba funcionales, de integración, de resiliencia y E2E. Registrar defectos con evidencia. Realizar retesting y validar correcciones.                                  |
| **Desarrollador (Dev)** | Backend Java / Spring  | Implementar y mantener las pruebas de integración. Brindar soporte técnico durante la ejecución de pruebas. Corregir defectos identificados. Verificar la configuración de índices y transacciones. |
| **DBA**                 | Administrador de BD    | Validar la creación y eficacia de los índices en la tabla `Movimientos`. Ejecutar análisis de consultas con `EXPLAIN ANALYZE`.                                                                       |
| **DevOps**              | Ingeniero de DevOps    | Gestionar los entornos Docker. Asegurar la disponibilidad del ambiente de pruebas y la correcta comunicación entre microservicios.                                                                   |
| **Product Owner (PO)**  | Responsable de negocio | Validar los criterios de aceptación. Priorizar defectos según impacto en el negocio financiero. Aprobar el cierre de pruebas.                                                                       |

---

# 12. Gestión de Defectos

## 12.1 Clasificación de Severidad

| Severidad        | Impacto                                                                               | Ejemplos en esta feature                                                                                                                                                                        |
| ---------------- | ------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Crítico (S1)** | Bloquea completamente el flujo principal del sistema. No existe solución alternativa. | El saldo de la cuenta no se actualiza tras un movimiento. Datos parciales persistidos por fallo en la atomicidad. El retiro con saldo insuficiente no es rechazado. El microservicio no levanta. |
| **Alto (S2)**    | Afecta funcionalidades principales, pero existe una alternativa parcial.              | El saldo puede quedar negativo. La modificación directa del saldo es posible desde `/cuentas/{id}`. El Circuit Breaker no activa el fallback. El reporte devuelve movimientos fuera del rango. |
| **Medio (S3)**   | Impacta funcionalidades secundarias o genera inconsistencias menores.                 | El reporte no incluye el nombre del cliente en el fallback. Mensajes de error no descriptivos. Validaciones incorrectas en campos opcionales. Logs sin `correlationId`.                        |
| **Bajo (S4)**    | Impacto mínimo, principalmente en la experiencia del consumidor de la API.            | Formato inconsistente en los mensajes de error. Campos innecesarios en la respuesta del reporte. Descripciones incorrectas en Swagger.                                                         |

---

## 12.2 Flujo de Gestión de Defectos

- Los defectos deben ser registrados en la herramienta de seguimiento del proyecto, incluyendo:
  - descripción del problema,
  - pasos para reproducir,
  - resultado esperado vs. resultado actual,
  - evidencia (payloads HTTP, estado de la base de datos, logs, capturas de pantalla).
- Los defectos críticos y altos deben ser comunicados inmediatamente al equipo de desarrollo.
- Una vez corregido el defecto, el QA responsable debe ejecutar el **retesting** correspondiente.
- Un defecto se considera cerrado únicamente cuando la corrección ha sido validada en el ambiente de pruebas.

---

# 13. Métricas de Seguimiento

| Métrica                              | Descripción                                                                                                  | Objetivo / Referencia                          |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------ | ---------------------------------------------- |
| **Cobertura de ejecución**           | Porcentaje de casos de prueba ejecutados respecto al total planificado.                                      | ≥ 95% de ejecución total                       |
| **Tasa de éxito (Pass Rate)**        | Porcentaje de casos de prueba que pasan respecto a los ejecutados.                                           | ≥ 90% de pruebas exitosas                      |
| **Densidad de defectos**             | Número de defectos identificados por historia de usuario o módulo.                                           | Monitoreo continuo                             |
| **Defectos por severidad**           | Distribución de defectos según su severidad (Crítico, Alto, Medio, Bajo).                                    | 0 defectos críticos al cierre                  |
| **Tiempo de resolución de defectos** | Tiempo promedio desde el reporte de un defecto hasta su cierre.                                              | Críticos ≤ 24h / Altos ≤ 48h                   |
| **Cobertura funcional**              | Porcentaje de criterios de aceptación validados mediante pruebas.                                            | 100% de cobertura                              |
| **Reapertura de defectos**           | Porcentaje de defectos que vuelven a abrirse después de ser cerrados.                                        | ≤ 5%                                           |
| **Integridad transaccional**         | Número de escenarios de rollback validados exitosamente respecto al total planificado.                       | 100% de escenarios de rollback validados       |
| **Tiempo de respuesta de reportes**  | Tiempo de respuesta del endpoint `/reportes` con volúmenes de datos representativos.                         | ≤ 2 segundos con índice aplicado               |
| **Efectividad de pruebas**           | Relación entre defectos encontrados en QA vs defectos encontrados en producción.                             | Maximizar detección en QA                      |
