# Route Optimizer - Technical Challenge

## Descripción

Este proyecto resuelve el desafío de cargar tiempos de viaje entre ubicaciones desde un archivo CSV y calcular la ruta más rápida entre dos ubicaciones, soportando multiempresa y autenticación (deshabilitada en modo demo). Incluye backend en Java Spring Boot y frontend en Vue 3, ambos dockerizados y listos para producción o pruebas locales.

---

## Despliegue público gratuito (Render, Vercel, Netlify)

### Backend (Java Spring Boot)
- **Render.com** (recomendado):
  - El Dockerfile usa multi-stage build, por lo que Render puede construir y ejecutar el backend sin requerir el JAR precompilado.
  - Solo sube tu repo y Render ejecutará el Dockerfile correctamente.
  - No necesitas la carpeta `target/` en el repo.
- **Railway/Fly.io:**
  - También soportan Docker multi-stage build.

### Frontend (Vue)
- **Vercel o Netlify** (recomendado):
  - Sube el contenido de `frontend/` a un repo en GitHub.
  - Conecta el repo desde el dashboard de Vercel/Netlify.
  - Build command: `npm run build`  
    Output dir: `dist`
  - Obtendrás una URL pública para tu frontend.
- **GitHub Pages:**
  - Solo para sitios estáticos. Si usas history mode, configura el rewrite a `index.html`.

### Tips de configuración para despliegue público
- **CORS:**
  - Asegúrate de que el backend permita el origen del frontend público (por ejemplo, `https://tufrontend.vercel.app`).
- **Endpoints:**
  - En producción, configura el frontend para apuntar a la URL pública del backend.
- **Variables de entorno:**
  - Usa variables de entorno en Vercel/Netlify/Render para configurar endpoints y secrets.

---

## Estructura del Proyecto

```