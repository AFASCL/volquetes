# Plan técnico — Issue 3: Gestión de Pedidos (Tickets T1–T5)

> Agente 3 — Tech Planner. Fuente: Issue 3 READY + respuestas PRD Challenger (P0/P1/P2).  
> `.github/workflow.md` y `STATES_AND_LABELS.md` no presentes en repo; labels sugeridos genéricos (tipo/área/prioridad).

---

## A) Tabla resumen T1..Tn

| Ticket | Título | Área | Deps | Estimación |
|--------|--------|------|------|------------|
| **T1** | DB: tablas pedidos, choferes, camiones + constraints | DB | — | 1–2 días |
| **T2** | Backend: CRUD pedidos + selectores choferes/camiones | Backend | T1 | 1–2 días |
| **T3** | Backend: transiciones de estado + permisos Admin/Chofer | Backend | T2 | 1–2 días |
| **T4** | Frontend: ABM pedidos (listado, alta, edición, detalle) | Frontend | T2 | 1–2 días |
| **T5** | Frontend: cambio de estado + asignación (chofer/camión/fecha) | Frontend | T3, T4 | 1–2 días |

---

## B) Detalle completo de cada ticket

---

### T1 — DB: tablas pedidos, choferes, camiones + constraints

**Objetivo**  
Dejar soporte en base de datos para pedidos (cliente, volquete, dirección de entrega, estado, fechas, asignación chofer/camión) y para entidades mínimas choferes y camiones, con constraints que aseguren unicidad de volquete en pedidos activos.

**Alcance**
- Crear tabla `choferes` (id, nombre u otro identificador mínimo).
- Crear tabla `camiones` (id, patente o nombre mínimo).
- Crear tabla `pedidos` con: `id`, `cliente_id` (FK → clientes), `volquete_id` (FK → volquetes), `direccion_entrega` (VARCHAR, puede diferir de cliente), `estado` (CHECK: NUEVO, ASIGNADO, ENTREGADO, RETIRADO, CERRADO, CANCELADO), `fecha_creacion`, `fecha_entrega_prevista`, `fecha_entrega_real` (nullable), `fecha_retiro_real` (nullable), `chofer_id` (FK → choferes, nullable), `camion_id` (FK → camiones, nullable), `created_at`/`updated_at` si se usan en el resto del modelo.
- Constraint: un mismo `volquete_id` no puede aparecer en más de un pedido con `estado` IN ('NUEVO','ASIGNADO','ENTREGADO') (índice único parcial o equivalente).
- Índices: estado, cliente_id, volquete_id para listados/filtros.
- Script incremental + rollback manual (convención `YYYYMMDD_3_pedidos.sql` / `__rollback.sql`).

**No alcance**
- Lógica de aplicación (backend).
- Historial de estados de pedido (opcional v1; si no se implementa, no hay tabla adicional en T1).

**Criterios de aceptación (Given/When/Then)**
- **Given** el script incremental ejecutado sobre el esquema actual, **When** se inserta un pedido con estado NUEVO/ASIGNADO/ENTREGADO para un volquete ya usado en otro pedido activo, **Then** la base rechaza la inserción (constraint/unique).
- **Given** el script ejecutado, **When** se inserta un pedido con cliente_id y volquete_id existentes y estado NUEVO, **Then** se persiste correctamente.
- **Given** el script de rollback, **When** se ejecuta, **Then** se eliminan tablas en orden (pedidos → choferes/camiones o el orden que evite violar FK) sin dejar objetos huérfanos.

**Tareas técnicas**
- Redactar script `db/scripts/YYYYMMDD_3_pedidos.sql`: CREATE TABLE choferes, camiones, pedidos; CHECK estado; UNIQUE parcial (volquete_id) WHERE estado IN (...); FKs; índices.
- Redactar script __rollback.sql (DROP TABLE pedidos, choferes, camiones en orden).
- Actualizar `memory-bank/06-data-model.md` con descripción de tablas pedidos, choferes, camiones (PK, campos, FKs, constraints, índices).

**Labels sugeridos**  
`type: feature` · `area: db` · `priority: high`

**Dependencias**  
Ninguna.

**Definition of Done**
- [ ] Scripts en `db/scripts/` ejecutables sin error.
- [ ] Constraint de unicidad volquete activo verificada con un intento de duplicado.
- [ ] 06-data-model.md actualizado con las nuevas tablas.

---

### T2 — Backend: CRUD pedidos + selectores choferes/camiones

**Objetivo**  
Exponer API REST para alta, listado paginado con filtros, detalle y edición de pedidos, y endpoints livianos para selector de choferes y camiones, con validaciones de negocio (cliente y volquete obligatorios; volquete no en otro pedido activo).

**Alcance**
- Entidad JPA `Pedido` (y si aplica `Chofer`, `Camion`) mapeando tablas de T1.
- DTOs: PedidoRequest (clienteId, volqueteId, direccionEntrega, fechaEntregaPrevista; para edición todos los editables), PedidoResponse (id, clienteId/clienteResumen, volqueteId/volqueteResumen, direccionEntrega, estado, fechas, choferId, camionId, etc.).
- Repository: PedidoRepository (JPA); consultas para listado con filtros (estado, clienteId, volqueteId) y para “existe pedido activo con este volquete”.
- Service: crear (estado inicial NUEVO), obtener por id, listar paginado con filtros, actualizar (PUT). Validaciones: cliente existe, volquete existe, volquete no en otro pedido activo (409/422).
- Controller: `GET /api/pedidos` (page, size, sort, estado, clienteId, volqueteId), `GET /api/pedidos/{id}`, `POST /api/pedidos`, `PUT /api/pedidos/{id}`. Códigos 400/404/409/422 según contrato.
- Endpoints selectores: `GET /api/choferes/selector`, `GET /api/camiones/selector` (lista liviana id + nombre/patente para combos). Entidades/repos mínimos si no existen.
- Documentar contrato en `memory-bank/04-api-documentation.md` (pedidos + choferes/camiones selectores).
- Tests unitarios Service (crear, validaciones, listar); tests integración Controller (opcional pero recomendado).

**No alcance**
- Transiciones de estado (PATCH estado) → T3.
- Permisos por rol (Chofer vs Admin) → T3; en T2 puede asumirse mismo permiso para todos los endpoints de pedidos.

**Criterios de aceptación (Given/When/Then)**
- **Given** cliente y volquete existentes y volquete no en otro pedido activo, **When** POST con clienteId, volqueteId, direccionEntrega (y fechaEntregaPrevista si aplica), **Then** 201, pedido en estado NUEVO.
- **Given** pedido creado con volquete V, **When** POST otro pedido con el mismo volquete V y estado que lo mantenga activo, **Then** 409 o 422 con mensaje de volquete en uso.
- **Given** request sin clienteId o sin volqueteId, **When** POST, **Then** 422 (o 400) y mensaje claro.
- **Given** usuario autenticado, **When** GET /api/pedidos con filtros estado/cliente/volquete, **Then** 200 y lista paginada.
- **Given** pedido existente, **When** PUT con datos editables (cliente, direccion, volquete, fechas, etc.), **Then** 200 y datos actualizados.
- **Given** usuario autenticado, **When** GET /api/choferes/selector y GET /api/camiones/selector, **Then** 200 y listas para combos.

**Tareas técnicas**
- Crear dominio: Pedido, Chofer, Camion (entidades).
- Crear DTOs PedidoRequest, PedidoResponse, ChoferSelectorItem, CamionSelectorItem (o equivalente).
- PedidoRepository + método existsByVolqueteIdAndEstadoIn(volqueteId, estados activos).
- PedidoService: create (validar cliente, volquete, unicidad activo), findById, findAll(Pageable, filtros), update.
- PedidoController: GET list, GET id, POST, PUT.
- Chofer/Camion: Repository + Service + Controller selector (o un único controller de “recursos”).
- GlobalExceptionHandler: manejar conflictos (409) y validación (422).
- Actualizar 04-api-documentation.md con endpoints y DTOs.
- Tests: PedidoService (crear válido, rechazo sin cliente, rechazo volquete ocupado); Controller (al menos POST y GET list).

**Labels sugeridos**  
`type: feature` · `area: backend` · `priority: high`

**Dependencias**  
T1 (scripts DB aplicados).

**Definition of Done**
- [ ] CRUD pedidos funcional contra DB de T1.
- [ ] Selectores choferes/camiones respondiendo.
- [ ] Validaciones CA1–CA3 cubiertas (rechazos con código y mensaje).
- [ ] Contrato documentado en memory-bank 04.
- [ ] Tests mínimos pasando.

---

### T3 — Backend: transiciones de estado + permisos Admin/Chofer

**Objetivo**  
Implementar la máquina de estados del pedido (Nuevo → Asignado → Entregado → Retirado → Cerrado; Cancelado desde Nuevo/Asignado), actualizando estado del volquete e historial cuando corresponda, y diferenciar permisos: Admin (crear, editar, cambiar estado) y Chofer (cambiar estado y consultar).

**Alcance**
- Endpoint `PATCH /api/pedidos/{id}/estado` (o equivalente) con cuerpo: estado destino y, para Asignado, choferId, camionId, fechaEntregaPrevista.
- Reglas de transición:
  - NUEVO → ASIGNADO (requiere choferId, camionId, fechaEntregaPrevista); NUEVO → CANCELADO.
  - ASIGNADO → ENTREGADO; ASIGNADO → CANCELADO.
  - ENTREGADO → RETIRADO.
  - RETIRADO → CERRADO.
  - CERRADO y CANCELADO: no permitir más transiciones (422).
- Al pasar a ENTREGADO: actualizar volquete a EN_CLIENTE e insertar en volquete_estado_historial con origen PEDIDO.
- Al pasar a RETIRADO: actualizar volquete a DISPONIBLE (o EN_TRANSITO si se define) e insertar en historial con origen PEDIDO.
- Al pasar a CANCELADO (desde NUEVO o ASIGNADO): volquete a DISPONIBLE e insertar en historial con origen PEDIDO.
- Permisos: Admin puede POST/PUT pedidos y PATCH estado; Chofer puede GET list/detalle y PATCH estado (no crear ni editar datos del pedido). Documentar en 04 (permiso requerido por endpoint).
- Respuestas de error: 422 para transición no permitida o datos faltantes (ej. Asignado sin chofer/camión/fecha).

**No alcance**
- Historial de estados del pedido (tabla/auditoría) opcional; si no está en alcance v1, no implementar.
- Cambio de estado del volquete manual en paralelo (si el volquete pasa a FUERA_DE_SERVICIO, el pedido sigue su flujo; caso borde documentado en PRD).

**Criterios de aceptación (Given/When/Then)**
- **Given** pedido en NUEVO, **When** PATCH a ASIGNADO sin choferId/camionId/fecha, **Then** 422.
- **Given** pedido en NUEVO, **When** PATCH a ASIGNADO con choferId, camionId, fechaEntregaPrevista, **Then** 200 y pedido en ASIGNADO.
- **Given** pedido en ASIGNADO, **When** PATCH a ENTREGADO, **Then** 200, volquete en EN_CLIENTE e historial con origen PEDIDO.
- **Given** pedido en ENTREGADO, **When** PATCH a RETIRADO, **Then** 200, volquete en DISPONIBLE (o definido) e historial PEDIDO.
- **Given** pedido en RETIRADO, **When** PATCH a CERRADO, **Then** 200.
- **Given** pedido en NUEVO o ASIGNADO, **When** PATCH a CANCELADO, **Then** 200, volquete en DISPONIBLE e historial PEDIDO.
- **Given** pedido en CERRADO o CANCELADO, **When** PATCH a cualquier estado, **Then** 422.
- **Given** pedido en ENTREGADO, **When** PATCH a RETIRADO sin haber pasado por Entregado (ya cubierto por flujo), **Then** N/A; **Given** pedido en NUEVO, **When** PATCH a RETIRADO, **Then** 422.
- **Given** usuario con rol Chofer, **When** PATCH estado de un pedido, **Then** 200 si la transición es válida; **When** PUT del pedido, **Then** 403.

**Tareas técnicas**
- PedidoService: método cambiarEstado(pedidoId, estadoDestino, dto con chofer/camion/fecha para Asignado). Validar transición, actualizar pedido; si ENTREGADO/RETIRADO/CANCELADO, llamar a VolqueteService (o lógica) para actualizar estado e historial.
- DTO PedidoEstadoRequest (estado, choferId, camionId, fechaEntregaPrevista).
- Controller: PATCH /api/pedidos/{id}/estado con body anterior.
- Configuración de seguridad/permisos: endpoint PATCH permitido para Admin y Chofer; PUT/POST solo Admin. Documentar en 04.
- Tests: transiciones válidas e inválidas; verificación de actualización de volquete e historial en ENTREGADO, RETIRADO, CANCELADO.

**Labels sugeridos**  
`type: feature` · `area: backend` · `priority: high`

**Dependencias**  
T2 (CRUD y selectores disponibles).

**Definition of Done**
- [ ] Todas las transiciones definidas implementadas y rechazos 422 para inválidas.
- [ ] Volquete e historial actualizados según CA5–CA7.
- [ ] Permisos Admin vs Chofer documentados y aplicados (403 Chofer en PUT).
- [ ] Tests de transiciones y de integración volquete/historial pasando.

---

### T4 — Frontend: ABM pedidos (listado, alta, edición, detalle)

**Objetivo**  
Pantalla de gestión de pedidos: listado paginado con filtros (estado, cliente, volquete), alta con cliente, dirección de entrega y volquete, edición (Admin) de campos definidos y detalle con toda la información y trazabilidad necesaria.

**Alcance**
- Vista (ej. `PedidosAbm.vue` o equivalente): listado en tabla o cards, paginación, filtros por estado (y opcional cliente/volquete según API).
- Formulario de alta: selector de cliente (GET /api/clientes/selector), campo dirección de entrega (puede ser distinta a la del cliente), selector de volquete (GET /api/volquetes con filtro o selector; solo volquetes no ocupados en pedido activo si el backend expone esa info o se filtra en front).
- Formulario de edición (Admin): mismos campos editables (cliente, direccion_entrega, volquete, fecha_entrega_prevista, etc.) según contrato PUT; solo visible/usable para Admin.
- Detalle de pedido: cliente, dirección, volquete, modalidad (derivada del cliente), estado, fechas, chofer/camión si están asignados; historial de estados si el backend lo expone en v1.
- API client: módulo `services/api/pedidos.ts` (y si aplica choferes/camiones) con tipos TS alineados a 04. Manejo de errores 400/404/409/422.
- Ruta y menú: ruta protegida (ej. `/pedidos`), link en layout para Admin (y/o Chofer si también ve listado).
- UX: loading, empty, error + retry, feedback éxito/error en crear/editar.

**No alcance**
- Cambio de estado desde la UI (botones/transiciones) → T5.
- Formulario de asignación (chofer/camión/fecha) → T5.

**Criterios de aceptación (Given/When/Then)**
- **Given** usuario Admin, **When** entra a la vista de pedidos, **Then** ve listado paginado y puede filtrar por estado (y opcional cliente/volquete).
- **Given** usuario Admin, **When** crea pedido con cliente, dirección de entrega y volquete válidos, **Then** pedido creado y mensaje de éxito; lista se actualiza.
- **Given** usuario Admin, **When** intenta crear sin cliente o sin volquete, **Then** mensaje de error (422/400) claro en UI.
- **Given** backend devuelve 409 (volquete en uso), **When** usuario crea pedido con ese volquete, **Then** se muestra mensaje de volquete no disponible.
- **Given** pedido existente, **When** Admin edita y guarda, **Then** datos actualizados y feedback de éxito.
- **Given** usuario con permiso, **When** abre detalle de un pedido, **Then** ve cliente, dirección, volquete, modalidad (del cliente), estado, fechas y asignación si existe.

**Tareas técnicas**
- Tipos TS: PedidoRequest, PedidoResponse, estados, filtros (alineados a 04).
- services/api/pedidos.ts: listPedidos(params), getPedido(id), createPedido(body), updatePedido(id, body). Manejo de errores estándar.
- Vista: listado con tabla, filtros, paginación; modal o página de alta/edición con formulario; vista/popover de detalle.
- Selectores: reutilizar o consumir GET clientes/selector, GET volquetes (o endpoint que liste volquetes no ocupados si existe).
- Router: ruta /pedidos con meta.permisoRequerido según definición (Admin para ABM).
- Integración en menú Index (link “Pedidos” para Admin).
- Actualizar memory-bank 07 si se añaden convenciones (ej. rutas o permisos nuevos).

**Labels sugeridos**  
`type: feature` · `area: frontend` · `priority: high`

**Dependencias**  
T2 (API CRUD y selectores disponibles).

**Definition of Done**
- [ ] Listado, alta, edición y detalle funcionales contra API de T2.
- [ ] Filtros y paginación operativos.
- [ ] UX loading/empty/error y feedback en mutaciones.
- [ ] Solo Admin puede crear/editar; permisos alineados con backend.

---

### T5 — Frontend: cambio de estado + asignación (chofer/camión/fecha)

**Objetivo**  
Permitir al usuario (Admin o Chofer) cambiar el estado del pedido desde la UI, con flujo controlado según transiciones permitidas, e incluir formulario para completar datos de asignación (chofer, camión, fecha/hora) al pasar a Asignado.

**Alcance**
- En detalle o listado de pedidos: botones o select para “Siguiente estado” según estado actual (NUEVO → Asignado o Cancelado; ASIGNADO → Entregado o Cancelado; etc.).
- Al elegir “Asignado”: mostrar formulario (chofer, camión, fecha/hora entrega prevista) usando selectores GET /api/choferes/selector y GET /api/camiones/selector; enviar en el body del PATCH estado.
- Chofer puede acceder a la misma vista (o vista reducida) y cambiar estados; no ve o no puede usar botones de crear/editar pedido.
- Llamada a `PATCH /api/pedidos/{id}/estado` con el DTO definido en T3.
- Mensajes claros ante 422 (transición no permitida, datos faltantes para Asignado).
- Actualización del estado del volquete y del historial queda en backend (T3); el frontend solo refleja el nuevo estado tras el PATCH exitoso.

**No alcance**
- Historial de estados del pedido en UI (si el backend no lo expone en v1, no implementar).
- Dashboard específico chofer (hoja de ruta) como vista separada; puede ser la misma lista de pedidos con permisos Chofer.

**Criterios de aceptación (Given/When/Then)**
- **Given** pedido en NUEVO, **When** usuario elige “Asignar” y completa chofer, camión y fecha, **Then** se envía PATCH y el pedido pasa a ASIGNADO.
- **Given** pedido en NUEVO, **When** usuario elige “Asignar” y no completa datos, **Then** no se envía o se muestra error de validación.
- **Given** pedido en ASIGNADO, **When** usuario elige “Entregado”, **Then** PATCH exitoso y estado actualizado en UI.
- **Given** pedido en CERRADO, **When** usuario intenta cambiar estado, **Then** no se muestra opción o se muestra mensaje de que no se puede.
- **Given** usuario Chofer, **When** cambia estado de un pedido, **Then** puede hacerlo y no ve opciones de crear/editar pedido (o recibe 403 si intenta).

**Tareas técnicas**
- Lógica de “siguientes estados” permitidos por estado actual (mapeo en front o recibido del backend si se expone).
- Componente o sección “Cambiar estado”: botones o select + modal/form para Asignado (chofer, camión, fecha).
- services/api/pedidos.ts: cambiarEstado(id, body).
- Tipos: PedidoEstadoRequest (estado, choferId?, camionId?, fechaEntregaPrevista?).
- Integrar en la misma vista de pedidos (detalle o fila); permisos según rol (Chofer solo cambio de estado).
- Mensajes de error 422 traducidos a mensaje usuario (ej. “No se puede pasar a Retirado sin haber entregado”).

**Labels sugeridos**  
`type: feature` · `area: frontend` · `priority: high`

**Dependencias**  
T3 (PATCH estado y permisos); T4 (vista de pedidos donde se integra).

**Definition of Done**
- [ ] Todas las transiciones permitidas son accionables desde la UI.
- [ ] Form Asignado con chofer, camión y fecha funcional.
- [ ] Chofer puede cambiar estado sin poder crear/editar pedido.
- [ ] Errores 422 mostrados con mensaje claro.

---

## Notas para GitHub

- Copiar cada bloque “Detalle completo” como cuerpo de un Issue; el título del Issue puede ser el “Título” de la tabla (ej. “T1 — DB: tablas pedidos, choferes, camiones + constraints”).
- Ajustar labels según el esquema real del repo (ej. `area: database` si usan ese nombre).
- En el board, ordenar T1 → T2 → T3; T4 puede ir en paralelo a T3 una vez T2 está listo; T5 después de T3 y T4.
