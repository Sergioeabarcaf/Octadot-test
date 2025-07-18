# Backend - API REST para Rutas

## Descripción

Backend Java Spring Boot para cargar tiempos de viaje entre ubicaciones desde un archivo CSV y calcular la ruta más rápida entre dos ubicaciones. Soporta multiempresa y está preparado para integración con frontend dockerizado.

---

## Requisitos
- Docker y Docker Compose
- (Opcional) Java 21+ y Maven para desarrollo local

---

## Build multi-stage para despliegue en Render, Railway, Fly.io

El Dockerfile usa un **multi-stage build**: construye el JAR dentro del contenedor usando Maven y luego lo ejecuta en una imagen ligera. No necesitas compilar el JAR antes de hacer el build de la imagen.

### Ejemplo de build local
```bash
docker build -t ruta-backend .
docker run -p 8080:8080 ruta-backend
```

### Despliegue en Render
- Solo sube tu repo y Render ejecutará el Dockerfile correctamente.
- No necesitas el JAR precompilado ni la carpeta `target/` en el repo.

---

## Levantar solo el backend con Docker Compose

Desde la raíz del proyecto o desde la carpeta backend:

```bash
docker-compose up --build -d backend
```

El backend estará disponible en [http://localhost:8080](http://localhost:8080)

---

## Integración con frontend

Para integración completa, usa docker-compose desde la raíz:

```bash
docker-compose up --build -d
```

Esto levantará backend y frontend en la misma red de Docker.

---

## Testing

Para correr los tests del backend:
```bash
cd backend
mvn test
```

---

## Endpoints principales

- **POST** `/api/connections/upload` (carga CSV, multipart/form-data)
- **GET** `/api/routes/shortest?from=ORIGEN&to=DESTINO` (ruta más corta)
- **GET** `/api/connections/list` (debug)

---

## Troubleshooting
- Si tienes errores de CORS, asegúrate de que el backend permite los orígenes `http://localhost:8081` y `http://localhost:5173`.
- Si cambias código, ejecuta:
  ```bash
  mvn clean package -DskipTests
  docker-compose build backend
  docker-compose restart backend
  ```
- Revisa los logs con:
  ```bash
  docker-compose logs backend
  ```

--- 