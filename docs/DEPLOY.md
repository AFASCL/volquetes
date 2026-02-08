# Despliegue: Supabase + Render + Vercel

Guía para publicar el sistema de volquetes en:

- **Base de datos:** Supabase (PostgreSQL gratis)
- **Backend (API):** Render
- **Frontend (Vue):** Vercel, apuntando al backend en Render

---

## 1. Base de datos en Supabase

### 1.1 Crear proyecto

1. Entrá a [supabase.com](https://supabase.com) y creá un proyecto (plan Free).
2. Anotá la **Connection string** (URI) que te da Supabase:
   - En el dashboard: **Project Settings → Database**.
   - Usá **Connection string → URI** (modo “Transaction” o “Session” para no usar pooler si preferís).
   - Formato típico:  
     `postgresql://postgres.[ref]:[TU_PASSWORD]@aws-0-[region].pooler.supabase.com:6543/postgres`
   - Para JDBC en el backend necesitás:  
     `jdbc:postgresql://...` con el mismo host, puerto y base.  
     Ejemplo:  
     `jdbc:postgresql://aws-0-[region].pooler.supabase.com:6543/postgres?user=postgres.[ref]&password=...&sslmode=require`  
     O bien en Supabase suelen dar **Connection string** con “Direct connection” (puerto 5432). Usá esa y reemplazá el prefijo por `jdbc:postgresql://` y agregá `?sslmode=require` si hace falta.

### 1.2 Ejecutar scripts SQL

En **Supabase → SQL Editor** ejecutá los scripts en este orden (cada uno una vez):

| Orden | Script | Descripción |
|-------|--------|-------------|
| 1 | `db/scripts/20260201_clientes.sql` | Tabla clientes |
| 2 | `db/scripts/20260202_clientes_rename_col_nombre.sql` | Renombra columna a `nombre` |
| 3 | `db/scripts/20260203_volquetes.sql` | Tablas volquetes |
| 4 | `db/scripts/20260204_3_pedidos.sql` | Tablas pedidos, choferes, camiones |

Si algo falla por dependencias, revisá que el orden sea exactamente ese.

---

## 2. Backend en Render

### 2.1 Conectar el repo

1. Entrá a [render.com](https://render.com) y vinculá tu cuenta de GitHub/GitLab.
2. **New → Web Service**.
3. Elegí el repo `fuentes-volquetes` (o el que uses).
4. Si usás **Blueprint** (`render.yaml` en la raíz del repo):
   - En **Blueprint**, seleccioná el archivo `render.yaml` del repo.
   - Render creará el servicio `volquetes-backend` según ese archivo.
5. Si en cambio creás el servicio a mano:
   - **Root Directory:** `backend`
   - **Runtime:** Docker
   - **Dockerfile Path:** `Dockerfile` (relativo a `backend`)
   - **Build:** se usa el Dockerfile (no hace falta comando extra).
   - **Start:** también lo define el Dockerfile.

### 2.2 Variables de entorno en Render

En el servicio **volquetes-backend** → **Environment** agregá:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | JDBC URL de Supabase | `jdbc:postgresql://...supabase.com:5432/postgres?sslmode=require` |
| `DB_USERNAME` | Usuario de la base | El que te da Supabase (ej. `postgres.[ref]`) |
| `DB_PASSWORD` | Contraseña de la base | La que definiste en Supabase |
| `CORS_ALLOWED_ORIGINS` | Origen del frontend (Vercel) | `https://tu-app.vercel.app` |

- Para **varios orígenes** (app + previews):  
  `https://tu-app.vercel.app,https://tu-app-xxx.vercel.app`
- Sin barra final en las URLs.

### 2.3 Deploy

- Guardá los cambios y hacé **Deploy**.
- Cuando termine, anotá la URL del servicio, por ejemplo:  
  `https://volquetes-backend.onrender.com`  
  Esa URL es la que usará el frontend.

### 2.4 Probar el backend

- Health: `https://volquetes-backend.onrender.com/api/health`  
  Debería responder algo como `{"status":"UP"}`.

---

## 3. Frontend en Vercel

### 3.1 Conectar el repo

1. Entrá a [vercel.com](https://vercel.com) y vinculá GitHub/GitLab.
2. **Add New → Project** y elegí el repo del proyecto.
3. **Root Directory:** `frontend` (carpeta donde está el Vue/Vite).
4. **Framework Preset:** Vercel suele detectar Vite; si no, elegí Vite.
5. **Build Command:** `npm run build`
6. **Output Directory:** `dist`

### 3.2 Variable de entorno

En **Settings → Environment Variables** del proyecto de Vercel:

| Nombre | Valor | Entornos |
|--------|--------|----------|
| `VITE_API_BASE_URL` | URL del backend en Render (sin barra final) | Production, Preview si quieres |

Ejemplo:  
`VITE_API_BASE_URL=https://volquetes-backend.onrender.com`

- Es importante: sin `/` al final.
- En Vite las variables que empiezan por `VITE_` se inyectan en el build; por eso el frontend usa esta URL en producción.

### 3.3 Deploy

- Hacé **Deploy**.
- Cuando termine, anotá la URL del frontend, por ejemplo:  
  `https://tu-app.vercel.app`

### 3.4 CORS

- Volvé a Render → servicio **volquetes-backend** → **Environment**.
- Ajustá `CORS_ALLOWED_ORIGINS` para que incluya la URL real de Vercel (y si usás previews, las de preview).  
  Ejemplo:  
  `https://tu-app.vercel.app`  
  o varias separadas por coma.  
- Redeploy del backend si cambiaste CORS.

---

## 4. Resumen de URLs

| Qué | Dónde | URL típica |
|-----|--------|------------|
| Base de datos | Supabase | (solo connection string, no pública) |
| API (backend) | Render | `https://volquetes-backend.onrender.com` |
| App (frontend) | Vercel | `https://tu-app.vercel.app` |

El frontend en Vercel llama al backend en Render usando `VITE_API_BASE_URL`. El backend usa Supabase como PostgreSQL y permite orígenes CORS de la URL de Vercel.

---

## 5. Troubleshooting

- **CORS / “Failed to fetch”:**  
  Revisá que `CORS_ALLOWED_ORIGINS` en Render tenga exactamente la URL del frontend (con `https://`, sin barra final). Si tenés dominio custom en Vercel, sumalo también.

- **Backend no arranca / error de DB:**  
  Revisá `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en Render. La URL debe ser JDBC: `jdbc:postgresql://...` y en Supabase suele hacer falta `?sslmode=require`.

- **Frontend llama a localhost en producción:**  
  Asegurate de tener `VITE_API_BASE_URL` en Vercel y de haber hecho un **nuevo deploy** después de agregarla (el valor se fija en el build).

- **Scripts SQL fallan en Supabase:**  
  Ejecutalos en el orden indicado (1 → 2 → 3 → 4). Si un script ya se ejecutó, no lo vuelvas a correr sin revisar (o usá los rollback en `db/scripts/` si existen).
