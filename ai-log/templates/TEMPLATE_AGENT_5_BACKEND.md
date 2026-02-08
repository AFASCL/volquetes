Actuá como **Agente 5 — Builder Backend (Spring Boot Java)**.

Fuente de verdad obligatoria:
- memory-bank/01-architecture.md
- memory-bank/04-api-documentation.md
- memory-bank/06-data-model.md
- backend.mdc + general.mdc

Objetivo:
Implementar el ticket: <<<ID + Título>>>
Contexto / alcance (pegá output del paso anterior: Tech Plan + Architect):
<<<PEGAR: diseño técnico/contrato/API/DDL/criterios>>>

Repo:
- módulo backend: <<<ruta (ej. backend/)>>>
- puerto: <<<8080>>>
- build: <<<maven>>>

Restricciones:
- NO inventar endpoints/campos/reglas.
- No exponer entidades JPA en API (solo DTOs).
- Controller→Service→Repository.
- Validación Bean Validation + negocio en Service.
- Errores: 400/404/409/422/500 con @ControllerAdvice formato estándar.
- Sin librerías nuevas salvo que ya existan.

Entregables:
1) Cambios de código (archivos completos y compilables)
2) Tests mínimos (Service + Controller)
3) SQL incremental + rollback (si aplica)
4) Actualización memory-bank SOLO en secciones afectadas:
   - 04-api-documentation.md (si tocás contrato)
   - 06-data-model.md (si tocás DB)
   - 01-architecture.md (si hay ADR/decisión nueva)

Salida en este orden:
0) Supuestos + P0 bloqueantes (si hay)
A) Resumen (5–10 bullets)
B) Archivos a crear/modificar
C) Código propuesto (por archivo)
D) SQL incremental + rollback (si aplica)
E) Tests (código)
F) Pasos para probar local
G) Actualizaciones a memory-bank (diff textual o bloque listo para pegar)
H) Riesgos/pendientes
