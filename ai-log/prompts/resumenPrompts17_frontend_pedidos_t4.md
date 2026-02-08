# Pedidos — Frontend T4 (ABM pedidos: listado, alta, edición, detalle)

## Herramienta consultada
Cursor (Agente 6 — Builder Frontend, Vue 3 + Tailwind)

## Input (prompt)
- Ticket T4: Frontend ABM pedidos. Objetivo: pantalla de gestión con listado paginado y filtros (estado, cliente, volquete), alta con selector cliente + dirección de entrega + selector volquete, edición (Admin) de campos editables, detalle con cliente, dirección, volquete, modalidad (del cliente), estado, fechas, chofer/camión si existen.
- Alcance: tipos TS (PedidoRequest, PedidoResponse, estados, filtros); services/api/pedidos.ts (listPedidos, getPedido, createPedido, updatePedido); vista PedidosAbm.vue (tabla, filtros, paginación, modal alta/edición, detalle); selectores con GET clientes/selector y GET volquetes (listado para combo); ruta /pedidos protegida; link en menú para Admin; UX loading/empty/error + retry y feedback éxito/error en mutaciones.
- No alcance: cambio de estado (T5); formulario asignación chofer/camión (T5). Fuente: memory-bank 00, 04, 07, frontend.mdc, general.mdc.

## Output
- **Tipos:** `types/pedidos.ts` (PedidoEstado, PedidoRequest, PedidoResponse, PedidosPageResponse, ErrorResponse). En `types/clientes.ts`: ClienteSelectorItem y en clientes API: listClientesSelector() para combo.
- **API:** `services/api/pedidos.ts` con listPedidos(params), getPedido(id), createPedido(body), updatePedido(id, body) y mapeo de errores 400/404/409/422.
- **Vista:** PedidosAbm.vue con listado en tabla, filtros (estado, clienteId, volqueteId), paginación, modal alta/edición (cliente, direccionEntrega, volquete, fechaEntregaPrevista), panel o modal de detalle (clienteNombre, direccionEntrega, volqueteCodigoInterno, estado, fechas, choferNombre, camionPatente). Botones Nuevo/Editar solo para Admin (auth.hasRoleAdmin).
- **Router:** ruta children `pedidos` → PedidosAbm.vue. **Menú:** link "Pedidos" en Index.vue visible para Admin (v-if="auth.hasRoleAdmin").
- Memory-bank: sin cambios de contrato (04 ya documenta pedidos); 07 sin cambios de convención nuevos.

## Decisión humana
Implementación aceptada; modalidad en detalle se muestra como "Según cliente" o derivada si el backend incluye clienteTipo en respuesta de detalle en el futuro.

## Impacto en el repo
- Creados: `frontend/src/types/pedidos.ts`, `frontend/src/services/api/pedidos.ts`, `frontend/src/views/PedidosAbm.vue`. Modificados: `types/clientes.ts` (ClienteSelectorItem), `services/api/clientes.ts` (listClientesSelector), `router/index.ts` (ruta pedidos), `views/Index.vue` (link Pedidos para admin).
