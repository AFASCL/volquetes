# 02 — Components (Componentes y módulos)

> Objetivo: describir cómo se parte el sistema en piezas y responsabilidades.

## 1) Backend — módulos / paquetes
- `controller/`:
  - Responsabilidad:
  - Endpoints:
- `service/`:
  - Responsabilidad:
- `repository/`:
  - Responsabilidad:
- `domain/` o `entity/`:
  - Responsabilidad:
- `dto/`:
  - Responsabilidad:

### Dependencias internas
- <Módulo A> → <Módulo B> porque <razón>

## 2) Frontend — estructura sugerida
- `src/views/`: pantallas
- `src/components/`: componentes reutilizables
- `src/stores/`: Pinia
- `src/services/api/`: cliente API y recursos
- `src/composables/`: lógica reusable (hooks)

### Componentes base (si existen)
- `<DataTable />`:
- `<Dialog />`:
- `<Toast />`:

## 3) Integraciones externas (si aplica)
- <Sistema/Servicio>:
  - Propósito:
  - Tipo (REST/SOAP/cola):
  - Credenciales / env vars:
