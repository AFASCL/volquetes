# T4 — Backend: Transición de estado + historial (Builder Backend)

## Herramienta consultada
Cursor — Agente 5 (Builder Backend), según AGENTS.md.

## Input (prompt)

Actuá como **Agente 5 — Builder Backend (Spring Boot Java)**.

Fuente de verdad obligatoria: memory-bank/01-architecture.md, 04-api-documentation.md, 06-data-model.md, backend.mdc + general.mdc.

Objetivo: Implementar el ticket **T4 — Backend: Transición de estado + historial**.

Contexto/alcance (Tech Plan + Architect): Permitir cambiar el estado del volquete (DISPONIBLE, EN_CLIENTE, EN_TRANSITO, FUERA_DE_SERVICIO) y persistir cada cambio en historial. EN_TRANSITO puede ser sin pedido. Endpoint(s) para transición de estado (ej. PATCH /api/volquetes/{id}/estado). Reglas de negocio: estados permitidos, transiciones válidas; origen opcional (manual / pedido). Persistir en volquete_estado_historial en cada cambio; actualizar estado_actual en volquetes. Respuestas 404, 422 para transición inválida. No alcance: flujo completo de pedidos, reportes.

Criterios de aceptación: volquete DISPONIBLE → transición a EN_CLIENTE/EN_TRANSITO → estado actualizado e historial insertado; cualquier estado → FUERA_DE_SERVICIO → actualizado y registrado; EN_TRANSITO sin pedido → aceptado (origen MANUAL); transición no permitida → 422; volquete inexistente → 404.

Tareas técnicas: Service de transición de estado; validación de transiciones; escritura en tabla de historial y actualización de volquetes; documentar contrato en 04-api-documentation; tests unitarios (transiciones válidas e inválidas) y de integración.

Repo: backend/, puerto 8080, Maven. Restricciones: NO inventar endpoints/campos/reglas; no exponer entidades JPA; Controller→Service→Repository; Bean Validation + negocio en Service; errores 400/404/409/422/500 con @ControllerAdvice; sin librerías nuevas.

Entregables: cambios de código, tests mínimos, SQL si aplica, actualización memory-bank solo en secciones afectadas. Salida: Supuestos, Resumen, Archivos, Código, SQL, Tests, Pasos probar local, Memory-bank, Riesgos.

## Output (resumen y extractos)

La IA implementó en el repo:

- **Domain:** OrigenEstado enum (MANUAL, PEDIDO); VolqueteEstadoHistorial entity (id, volquete FK, estadoDesde, estadoHasta, fechaHora, origen opcional) con @PrePersist para fechaHora.
- **DTO:** VolqueteEstadoRequest (estado @NotNull, origen opcional); Bean Validation en estado.
- **Repository:** VolqueteEstadoHistorialRepository (JpaRepository estándar).
- **Service:** cambiarEstado(id, request) en VolqueteService: busca volquete (404 si no existe), actualiza estado_actual, inserta en historial (estadoDesde = anterior, estadoHasta = nuevo, origen opcional), todo en @Transactional.
- **Controller:** PATCH /api/volquetes/{id}/estado con @Valid VolqueteEstadoRequest; 200 con VolqueteResponse.
- **Errores:** 400 (Bean Validation estado/origen inválido), 404 (volquete no encontrado), 422 (reservado para futuras reglas). GlobalExceptionHandler ya cubre estos casos.
- **Tests:** VolqueteServiceTest: cambiarEstado ok (actualiza estado y guarda historial), notFound 404, mismo estado registra historial, sin origen registra NULL. VolqueteControllerTest: PATCH 200 con origen, PATCH 200 sin origen, PATCH 404.
- **Memory-bank:** Actualizado 04-api-documentation.md con nota sobre reglas de transición (v1): se permiten todas las transiciones entre los 4 estados; cambiar al mismo estado es válido y se registra; EN_TRANSITO puede ser sin pedido; 422 reservado para futuras reglas.

No SQL en T4 (T1 ya entregó scripts).

## Decisión humana
(Pendiente: aceptar implementación y marcar T4 como cerrado en el board.)

## Impacto en el repo
- Backend: domain (OrigenEstado, VolqueteEstadoHistorial), dto (VolqueteEstadoRequest), repository (VolqueteEstadoHistorialRepository), service (método cambiarEstado), controller (PATCH endpoint); tests VolqueteServiceTest y VolqueteControllerTest ampliados.
- memory-bank/04-api-documentation.md (nota sobre reglas de transición v1).
