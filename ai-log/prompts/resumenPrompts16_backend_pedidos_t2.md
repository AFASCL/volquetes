# Pedidos — Backend T2 (CRUD pedidos + selectores choferes/camiones)

## Herramienta consultada
Cursor (Agente 5 — Builder Backend)

## Input (prompt)
- Ticket T2: Backend CRUD pedidos + selectores choferes/camiones. Objetivo: API REST para alta, listado paginado con filtros (estado, clienteId, volqueteId), detalle, edición de pedidos; GET /api/choferes/selector y GET /api/camiones/selector. Validaciones: cliente y volquete obligatorios; volquete no en otro pedido activo (409).
- Alcance: entidades Pedido, Chofer, Camion; DTOs PedidoRequest, PedidoResponse, ChoferSelectorItemResponse, CamionSelectorItemResponse; PedidoRepository (JpaSpecificationExecutor, existsByVolqueteIdAndEstadoIn); PedidoService (create, findById, findAll con filtros, update); ChoferService/CamionService (findSelectorItems); controllers y GlobalExceptionHandler (404/409/422). Tests Service y Controller.
- No alcance: PATCH estado (T3); permisos Admin vs Chofer (T3). Fuente: memory-bank 01, 04, 06, backend.mdc.

## Output
- **Dominio:** PedidoEstado enum; entidades Pedido, Chofer, Camion; excepciones PedidoNotFoundException, PedidoValidationException, PedidoConflictException.
- **DTOs:** PedidoRequest (clienteId, volqueteId, direccionEntrega, fechaEntregaPrevista); PedidoResponse (id, clienteId, volqueteId, direccionEntrega, estado, fechas, choferId, camionId + opcionales clienteNombre, volqueteCodigoInterno, choferNombre, camionPatente); ChoferSelectorItemResponse, CamionSelectorItemResponse.
- **Repositorios:** PedidoRepository (existsByVolqueteIdAndEstadoIn, existsByVolqueteIdAndEstadoInAndIdNot), ChoferRepository, CamionRepository (findAllByOrderByNombreAsc/PatenteAsc).
- **Servicios y controladores:** PedidoController (GET list con filtros, GET id, POST, PUT); ChoferController (GET /selector); CamionController (GET /selector). GlobalExceptionHandler con handlers para pedido (404, 422, 409).
- **Tests:** PedidoServiceTest (create ok, 404 cliente/volquete, 409 volquete en uso, 422 dirección vacía, findById 404); PedidoControllerTest (POST 201, GET list 200, GET id 200). Sin cambios en memory-bank (contrato ya en 04).

## Decisión humana
Implementación aceptada; listado puede optimizarse con EntityGraph para N+1 en iteración posterior.

## Impacto en el repo
- Múltiples archivos nuevos en backend: domain (PedidoEstado, Pedido, Chofer, Camion, excepciones), dto (PedidoRequest, PedidoResponse, ChoferSelectorItemResponse, CamionSelectorItemResponse), repository (Pedido, Chofer, Camion), service (Pedido, Chofer, Camion), controller (Pedido, Chofer, Camion); modificado GlobalExceptionHandler. Tests PedidoServiceTest, PedidoControllerTest.
