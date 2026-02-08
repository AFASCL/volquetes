# Agente 7 — Verifier: Revisión de los 3 issues P0 desarrollados

> Entrada: Clientes (ABM), Volquetes (inventario + estados + export), Pedidos (CRUD + flujo estados T1–T5).  
> Fuente: memory-bank 04, 06, 07; docs tech-plan y arch pedidos; código backend/frontend/db.

---

## Alcance revisado

| Issue / Área   | Backend | Frontend | DB scripts | Contrato 04 |
|----------------|---------|----------|------------|-------------|
| **Clientes**   | ✅ CRUD, selector, GlobalExceptionHandler | ✅ ClientesAbm, modal, paginación | ✅ 20260201, 20260202 | ✅ |
| **Volquetes**  | ✅ CRUD, PATCH estado, export, historial | ✅ VolquetesAbm, VolquetesEstado, QR | ✅ 20260203 | ✅ |
| **Pedidos**    | ⚠️ CRUD + selectores; **falta PATCH /estado** | ✅ PedidosAbm, filtros, cambio estado, asignación | ✅ 20260204 | ✅ (contrato existe; endpoint no implementado) |

---

## A) Checklist PR

### Bloqueantes

1. **PATCH /api/pedidos/{id}/estado no implementado (T3 backend)**  
   - El frontend llama `PATCH /api/pedidos/{id}/estado` con `PedidoEstadoRequest` (estado; choferId, camionId, fechaEntregaPrevista para ASIGNADO).
   - En el backend: `PedidoController` solo tiene GET, POST, PUT. No existe el endpoint de cambio de estado.
   - **Acción:** Implementar en backend T3: `PedidoService.cambiarEstado(id, PedidoEstadoRequest)`, transiciones permitidas (NUEVO→ASIGNADO/CANCELADO; ASIGNADO→ENTREGADO/CANCELADO; etc.), actualización de volquete/historial cuando corresponda; `PedidoController`: `@PatchMapping("/{id}/estado")` y DTO `PedidoEstadoRequest`. Hasta que exista, el flujo "Cambiar estado" en la UI fallará en producción.

2. **Trazabilidad Issue en commits/scripts**  
   - Revisar que los commits y los comentarios de los scripts SQL referencien el Issue (ej. "Issue 1 Clientes", "Issue 3 T1"). Si no está en historial reciente, no bloquea merge pero debe adoptarse de aquí en adelante.

### No bloqueantes

- **04-api-documentation.md vs ClienteResponse:** En 04 se documenta en algún lugar `razonSocial` para GET clientes/{id}; el modelo actual usa `nombre`. Confirmar que el JSON expuesto sea `nombre` y que 04 esté alineado (o actualizar 04).
- **Tests de integración para PATCH pedidos/estado:** Cuando se implemente T3, añadir test en `PedidoControllerTest` para PATCH /api/pedidos/{id}/estado (200, 404, 422 por transición inválida).
- **Frontend — VolquetesEstado:** Ya corregido el error de compilación (template literal en `:message`); sin otros hallazgos bloqueantes.
- **CORS / env:** Para producción (Render + Vercel) ya documentado en docs/DEPLOY.md; no aplica a esta revisión de código.

---

## B) Plan de QA para TST

Ejecutar en entorno tipo TST (backend + frontend + DB con scripts aplicados).

### Clientes

- [ ] **Listado:** GET /api/clientes con page, size → 200 y estructura Page.
- [ ] **Alta:** POST con nombre, tipo, opcionales → 201; luego GET por id → 200 con mismos datos.
- [ ] **Edición:** PUT /api/clientes/{id} con cambios → 200; GET → datos actualizados.
- [ ] **Eliminación:** DELETE /api/clientes/{id} → 204; GET → 404.
- [ ] **Validación:** POST sin nombre → 422 (o 400) con mensaje claro.
- [ ] **UI:** Listado, paginación, modal alta/edición, botones Editar/Eliminar, feedback éxito/error.

### Volquetes

- [ ] **Listado:** GET /api/volquetes (y con ?estado=DISPONIBLE) → 200.
- [ ] **Alta:** POST con codigoInterno, codigoExterno → 201; estado por defecto DISPONIBLE.
- [ ] **Códigos duplicados:** POST mismo codigoInterno o codigoExterno → 409.
- [ ] **PATCH estado:** PATCH /api/volquetes/{id}/estado con { "estado": "EN_CLIENTE", "origen": "MANUAL" } → 200; GET → estado actualizado; verificar fila en volquete_estado_historial.
- [ ] **Export:** GET /api/volquetes/export → 200, Content-Type CSV, descarga con cabeceras correctas.
- [ ] **UI:** ABM, cambio de estado (si se usa en vista), export, QR; vista Estado inventario con filtros por estado.

### Pedidos

- [ ] **Listado y filtros:** GET /api/pedidos con estado, clienteId, volqueteId → 200 y resultados coherentes.
- [ ] **Alta:** POST con clienteId, volqueteId, direccionEntrega (y opcional fechaEntregaPrevista) → 201; pedido en estado NUEVO.
- [ ] **Volquete en uso:** POST otro pedido con el mismo volquete (en estado activo) → 409 (o 422) con mensaje de volquete ocupado.
- [ ] **Detalle y edición:** GET /api/pedidos/{id} → 200; PUT con datos editables → 200.
- [ ] **Selectores:** GET /api/choferes/selector y GET /api/camiones/selector → 200 y arrays con id + nombre/patente.
- [ ] **Cambio de estado (cuando backend T3 esté implementado):**  
  - PATCH /api/pedidos/{id}/estado con estado ASIGNADO y choferId, camionId, fechaEntregaPrevista → 200; luego ENTREGADO, RETIRADO, CERRADO según flujo; CANCELADO desde NUEVO o ASIGNADO.  
  - Transición inválida (ej. NUEVO → ENTREGADO) → 422 (o 400).  
- [ ] **UI:** Listado, filtros, alta/edición (Admin), detalle, modal "Cambiar estado" con select próximo estado y formulario ASIGNADO (chofer, camión, fecha); mensajes de error claros.

### Casos borde y errores

- [ ] GET /api/clientes/999 → 404 y cuerpo de error con code/message/timestamp.
- [ ] GET /api/volquetes/999 → 404.
- [ ] GET /api/pedidos/999 → 404.
- [ ] POST pedido con clienteId inexistente → 404 o 422.
- [ ] Request body JSON malformado → 400.

---

## C) Tests faltantes

- **Backend**
  - **PedidoService:** Cuando exista `cambiarEstado`, añadir tests: transición válida (NUEVO→ASIGNADO con chofer/camión/fecha), transición inválida (422), pedido no encontrado (404), validación ASIGNADO sin chofer/camión/fecha.
  - **PedidoController:** Test de integración para `PATCH /api/pedidos/{id}/estado` (200, 404, 422).
- **Frontend**
  - No hay Vitest en el repo; aceptable para esta entrega. Para futuras: tests de componentes críticos (por ejemplo formulario pedidos, selector de estado).

---

## D) Riesgos / trade-offs

- **Seguridad:** Auth/authz aún no implementado (memory-bank 01: "PENDIENTE"). En TST/PROD los endpoints quedan abiertos hasta que se añada protección por rol (Admin/Chofer) según 04.
- **Concurrencia:** Dos solicitudes simultáneas creando pedido con el mismo volquete: el constraint `uk_pedidos_volquete_activo` en DB evita duplicado; el backend puede devolver 409. Aceptable para v1.
- **Performance:** Listado pedidos con filtros y paginación está bien soportado; export volquetes sin paginación (todos). Para muchos volquetes, valorar en el futuro límite o streaming.
- **Deuda:** T3 backend (PATCH estado de pedido) es requisito de aceptación del Issue 3; sin él el flujo de estados en la UI no es end-to-end.

---

## E) Verificación DoD y trazabilidad

- **Scripts SQL:** Presentes en `db/scripts/` con convención YYYYMMDD y rollback; orden de ejecución documentado en DEPLOY.md.
- **Contrato API:** memory-bank/04 actualizado con clientes, volquetes, pedidos, selectores y PATCH volquetes/estado; PATCH pedidos/estado documentado pero no implementado.
- **Docs:** tech-plan Issue 3 (T1–T5), arch pedidos, PRD Challenger referenciables.
- **Commits:** Recomendación: incluir en mensajes referencia al Issue/ticket (ej. "Issue 3 T2: CRUD pedidos") para trazabilidad.

---

## F) Recomendación final

**REQUEST CHANGES**

- **Motivo:** El flujo de "Cambiar estado" del pedido (T3/T5) está implementado en frontend y documentado en API, pero el backend **no expone** `PATCH /api/pedidos/{id}/estado`. Eso rompe la aceptación del Issue 3 (transiciones de estado y asignación chofer/camión).
- **Acción obligatoria antes de considerar cerrados los 3 P0:**  
  Implementar en backend el endpoint `PATCH /api/pedidos/{id}/estado` (PedidoEstadoRequest), lógica de transiciones permitidas, actualización de estado del volquete e historial cuando corresponda, y tests mínimos (Service + Controller).
- **Tras implementar T3 backend:** Re-ejecutar el plan de QA de la sección B (ítems de Pedidos y Cambio de estado) y volver a pasar el Verifier para **APPROVE**.

---

*Documento generado por Agente 7 — Verifier (AFA SCL). Revisión de los 3 issues P0: Clientes, Volquetes, Pedidos.*
