# Frontend - Route Optimizer

## Descripción

Frontend Vue 3 para cargar archivos CSV y consultar rutas óptimas entre ubicaciones, integrando con el backend Java Spring Boot. Incluye drag & drop, diseño responsivo y dockerización lista para producción.

---

## Requisitos
- Docker y Docker Compose
- (Opcional) Node.js 20+ y npm para desarrollo local

---

## Levantar solo el frontend con Docker Compose

Desde la raíz del proyecto o desde la carpeta frontend:

```bash
docker-compose up --build -d frontend
```

El frontend estará disponible en [http://localhost:8081](http://localhost:8081)

---

## Integración con backend

Para integración completa, usa docker-compose desde la raíz:

```bash
docker-compose up --build -d
```

Esto levantará backend y frontend en la misma red de Docker.

---

## Desarrollo local (opcional)

```bash
cd frontend
npm install
npm run dev
# http://localhost:5173
```

---

## Troubleshooting
- Si tienes errores de CORS, asegúrate de que el backend permite el origen `http://localhost:8081`.
- Si cambias código, ejecuta:
  ```bash
  docker-compose build frontend
  docker-compose restart frontend
  ```
- Revisa los logs con:
  ```bash
  docker-compose logs frontend
  ```

---
