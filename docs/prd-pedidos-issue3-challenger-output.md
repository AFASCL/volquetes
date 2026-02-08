# PRD Challenger — Salida para Issue 3: Gestión de Pedidos

> Agente 1 — Analista de Requerimientos. Fuente: Issue 3 + memory-bank 00, 04, 06.  
> Nota: `.github/workflow.md` y `STATES_AND_LABELS.md` no están en el repo; análisis basado en 00-project-overview, 06-data-model, 04-api-documentation.

---

## 1) Resumen del objetivo

Implementar la **gestión de pedidos** de la v1: alta de pedidos con cliente, dirección de servicio y modalidad de precio (común/abono), asociando **un volquete obligatorio** por pedido, y flujo de estados (Nuevo → Asignado → Entregado → Retirado → Cerrado, más Cancelado) con reglas que impidan pedidos sin cliente/volquete, que un mismo volquete esté en más de un pedido activo, y que se avance solo en el orden definido (p. ej. no pasar a Retirado sin haber pasado por Entregado).

---

## 2) Alcance / No alcance

**Incluye:**
- Crear pedido con: cliente (obligatorio), dirección de servicio, modalidad de precio (común/abono), 1 volquete (obligatorio).
- Estados de pedido: Nuevo, Asignado, Entregado, Retirado, Cerrado, Cancelado.
- Transiciones de estado controladas (orden lógico; no Retirado sin Entregado).
- Regla de negocio: un volquete no puede estar en más de un pedido con estado Nuevo, Asignado o Entregado.
- Backend: modelo de datos (tabla(s) pedidos), endpoints CRUD + cambio de estado, validaciones de negocio.
- Frontend: ABM pedidos (listado, alta, edición si aplica) y cambio de estado controlado desde la UI.
- DB: scripts incrementales, FK a clientes y volquetes, constraints e índices necesarios.
- Integración con inventario: al avanzar estado del pedido (p. ej. Entregado / Retirado), actualizar estado del volquete (y opcionalmente registrar origen PEDIDO en historial), según definición de negocio.

**No incluye:**
- Pedidos sin volquete.
- Servicios especiales (v2).
- En este Issue no se define (queda para aclarar): si “Asignado” implica ya entidad asignación chofer/camión o solo nombre de estado; facturación; optimización de rutas; múltiples camiones.

---

## 3) Preguntas abiertas

### P0 (bloqueantes)

- **P0.1 — Dirección de servicio**  
  ¿La “dirección de servicio” del pedido es siempre la `direccion_principal` del cliente, o el usuario puede ingresar/editar una dirección distinta por pedido? (Impacta modelo de datos y pantalla de alta.)

- **P0.2 — Estado del volquete al crear pedido**  
  Al crear un pedido y asociar un volquete, ¿solo se permiten volquetes en estado `DISPONIBLE`? Si no, ¿cuáles estados están permitidos y qué hace el sistema con `volquetes.estado_actual` al crear el pedido (no cambiar, pasar a EN_TRANSITO, etc.)?

- **P0.3 — Cancelado**  
  ¿Desde qué estados se puede cancelar un pedido? (¿Solo Nuevo/Asignado o también Entregado?) ¿Al cancelar, el volquete vuelve a `DISPONIBLE` (o a otro estado) y se registra en `volquete_estado_historial` con origen `PEDIDO`?

- **P0.4 — Cerrado**  
  ¿“Cerrado” es únicamente alcanzable desde “Retirado” (flujo lineal), o hay otros caminos? ¿El sistema permite “cerrar” sin pasar por Retirado (excepciones operativas)?

### P1 (importantes)

- **P1.1 — Origen de la modalidad de precio**  
  ¿La modalidad (común/abono) del pedido se toma siempre del cliente (`clientes.tipo`) o se puede elegir/sobreescribir por pedido? (Impacta si en la tabla pedidos se guarda tipo o solo cliente_id.)

- **P1.2 — Quién crea y quién cambia estado**  
  ¿Solo Admin crea pedidos y cambia estados, o el Chofer también puede cambiar estados (p. ej. en dashboard) desde cierto estado? (Alineado con “Dashboard web mobile para chofer” del overview.)

- **P1.3 — “Asignado” y asignación chofer/camión**  
  El overview v1 incluye “Asignación de chofer y camión”. ¿El estado “Asignado” en este Issue significa “pedido asignado a chofer/camión” y por tanto este Issue incluye ya entidad relación pedido–chofer/camión, o “Asignado” es solo un nombre de estado y la asignación se modela en otro Issue?

- **P1.4 — Edición de pedido**  
  ¿Se puede editar un pedido ya creado (cliente, dirección, volquete, modalidad) o solo en estado Nuevo? ¿Qué campos son editables y hasta qué estado?

### P2 (deseables)

- **P2.1 — Fechas**  
  ¿Se exigen fechas (fecha de creación, fecha entrega, fecha retiro) en v1 o se dejan para una iteración posterior?

- **P2.2 — Eliminación física**  
  ¿Existirá DELETE de pedido (borrado físico) o solo cancelación (estado Cancelado)?

---

## 4) Criterios de aceptación mejorados (Given/When/Then)

- **CA1 — Creación con cliente y volquete**  
  **Given** un usuario con permiso de crear pedidos, **When** intenta crear un pedido sin cliente o sin volquete, **Then** el sistema rechaza la petición (422 o 400) con mensaje claro y no persiste el pedido.

- **CA2 — Creación válida**  
  **Given** cliente y volquete válidos (y reglas P0 resueltas: estado del volquete, dirección, modalidad), **When** crea un pedido con todos los datos obligatorios, **Then** el sistema persiste el pedido en estado Nuevo, asocia el volquete y respeta la regla de “un volquete solo en un pedido activo” (véase CA3).

- **CA3 — Un volquete, un pedido activo**  
  **Given** un volquete ya asociado a un pedido en estado Nuevo, Asignado o Entregado, **When** se intenta crear otro pedido o asignar ese mismo volquete a otro pedido activo, **Then** el sistema rechaza (409 o 422) con mensaje que indique que el volquete ya está en uso.

- **CA4 — Transiciones en orden**  
  **Given** un pedido en estado X, **When** el usuario intenta pasar a un estado Y que no es la siguiente transición permitida (p. ej. Retirado sin haber pasado por Entregado), **Then** el sistema rechaza (422) e indica la transición no permitida.

- **CA5 — Transición Entregado**  
  **Given** un pedido en estado Asignado (o el que se defina como anterior a Entregado), **When** se pasa a Entregado, **Then** el sistema actualiza el estado del volquete según regla de negocio (p. ej. a EN_CLIENTE) y, si aplica, registra en historial con origen PEDIDO.

- **CA6 — Transición Retirado**  
  **Given** un pedido en estado Entregado, **When** se pasa a Retirado, **Then** el sistema actualiza el estado del volquete (p. ej. a DISPONIBLE o EN_TRANSITO) y, si aplica, registra en historial con origen PEDIDO.

- **CA7 — Cancelado**  
  **Given** un pedido cancelable según regla de negocio (P0.3), **When** se cancela, **Then** el volquete queda liberado (estado según P0.3) y el pedido queda en Cancelado y no cuenta como “activo” para la regla de unicidad de volquete.

- **CA8 — Cerrado**  
  **Given** un pedido en estado Retirado (o el flujo que se defina), **When** se cierra, **Then** el pedido pasa a Cerrado y no cuenta como activo para la regla de unicidad de volquete.

- **CA9 — Listado y filtros**  
  **Given** un usuario con permiso, **When** consulta pedidos, **Then** puede ver listado (paginado) y, al menos, filtrar por estado de pedido (y opcionalmente por cliente/volquete según definición).

- **CA10 — Detalle y trazabilidad**  
  **Given** un pedido existente, **When** se consulta su detalle, **Then** se muestran cliente, dirección de servicio, volquete asociado, modalidad, estado actual y, si se define en v1, historial de cambios de estado.

---

## 5) Casos borde y errores

| Caso | Comportamiento esperado |
|------|-------------------------|
| Crear pedido con cliente inexistente (id inválido) | 404 o 422, mensaje “Cliente no encontrado”. |
| Crear pedido con volquete inexistente | 404 o 422, mensaje “Volquete no encontrado”. |
| Crear pedido con volquete ya en otro pedido activo | 409 o 422, mensaje que el volquete no está disponible. |
| Cambiar estado a uno no permitido (ej. Nuevo → Retirado) | 422, mensaje de transición no permitida. |
| Cambiar estado de un pedido ya Cerrado o Cancelado | 422 o 409, indicar que el pedido está cerrado/cancelado. |
| Volquete asociado pasa a FUERA_DE_SERVICIO manualmente | Definir: ¿el pedido sigue activo? ¿se puede seguir avanzando? (Recomendación: permitir según regla de negocio o 422 hasta resolver el volquete.) |
| Eliminar cliente que tiene pedidos | Según 04-api-documentation: 409 si tiene pedidos asociados. |
| Eliminar volquete con pedido activo | 409, no permitir borrado mientras exista pedido activo con ese volquete. |
| Concurrencia: dos usuarios asignan el mismo volquete a dos pedidos | Validación en backend + constraint/índice o lógica transaccional para evitar doble asignación; uno recibe 409/422. |

---

## 6) NFRs mínimos

- **Seguridad:** Endpoints de pedidos protegidos por auth; solo roles con permiso (Admin y/o Chofer según P1.2) pueden crear y cambiar estados; no exponer datos de otros clientes/operadores fuera de permiso.
- **Performance:** Listado paginado; consultas por estado y por volquete/cliente indexadas; evitar N+1 en detalle (pedido + cliente + volquete).
- **Auditoría:** Al menos registrar cambios de estado de pedido (quién/cuándo opcional en v1); volquete ya tiene historial con origen PEDIDO para trazar desde inventario.
- **UX:** Mensajes claros en validaciones (cliente/volquete obligatorio, volquete no disponible, transición no permitida); loading/error/empty en listados; feedback en mutaciones (éxito/error).

---

## 7) Definition of Ready (checklist)

- [ ] P0.1–P0.4 resueltas y reflejadas en alcance/criterios.
- [ ] Decisión sobre P1.1 (modalidad desde cliente vs. por pedido) documentada.
- [ ] Decisión sobre P1.2 (quién crea/cambia estado) y P1.3 (Asignado vs. asignación chofer/camión) documentada.
- [ ] Modelo de datos (tabla(s) pedidos, FK, estados) alineado con 06-data-model y con reglas de volquete activo.
- [ ] Contrato API (endpoints, DTOs, códigos 400/404/409/422) documentado o enlazado (memory-bank 04).
- [ ] Matriz de transiciones de estado de pedido definida (desde qué estado se puede ir a cuál).
- [ ] Comportamiento de volquete al crear pedido, al Entregado, al Retirado y al Cancelado definido y documentado.

---

## 8) Veredicto final

**NEEDS-INFO**

Las preguntas **P0.1 a P0.4** son bloqueantes para diseño e implementación (modelo, API y flujos). Hasta no cerrarlas, el Issue no está listo para Tech Planner/Architect sin asumir decisiones de negocio. P1 y P2 pueden resolverse en paralelo o en refinamiento siguiente.
