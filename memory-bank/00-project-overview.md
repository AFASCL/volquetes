# 00 — Project Overview (Visión general)

> Objetivo: que cualquier dev nuevo entienda qué es este sistema y qué incluye la v1 en 5 minutos.

## 1) Elevator pitch
- **Producto/sistema:** Sistema de Gestión de Volquetes
- **Para quién:** Empresa de volquetes (admin y choferes)
- **Problema que resuelve:** Falta de control integral de inventario, pedidos, pagos y logística
- **Valor:** Visibilidad en tiempo real, reducción de errores operativos y mejor gestión del negocio

## 2) Alcance de la v1 (MVP)
Incluye:
- Gestión de clientes
- Control de inventario de volquetes
- Gestión de pedidos (entrega / retiro)
- Asignación de chofer y camión
- Dashboard para chofer (hoja de ruta)
- Control de pagos y semáforo de vencimientos
- Reportes básicos y exportación a Excel

No incluye:
- Optimización avanzada de rutas
- Firma digital y envío de PDFs
- Ventas/servicios especiales (demoliciones, alquiler maquinaria)
- Múltiples camiones simultáneos (más de 1)

## 3) Stakeholders y roles
- **Sponsor / Dueño de negocio:** Empresa de volquetes
- **Product/Tech Lead:** Pablo Martini
- **Usuarios principales:** Admin, Chofer

## 4) Entornos
- **TST:** local / entorno de pruebas
- **PROD:** a definir
- Deploy: **manual** (ver `.github/workflow.md`)

## 5) Repos / módulos
- Backend: `/backend` (Spring Boot)
- Frontend: `/frontend` (Vue 3)
- DB scripts: `/db/scripts`

## 6) Glosario
- **Volquete:** Contenedor para residuos
- **Pedido:** Solicitud de entrega o retiro de volquete
- **Camión + pluma:** Unidad operativa para mover volquetes
- **En calle:** Volquete entregado al cliente
- **Semáforo de vencimientos:** Indicador visual de deuda/pagos

## 7) Links clave
- Workflow del equipo: `.github/workflow.md`
- Estados/labels: `.github/STATES_AND_LABELS.md`
- Agentes IA: `AGENTS.md`
- Uso IA: `AI_USAGE.md`
