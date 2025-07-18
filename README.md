# Route Optimizer - Technical Challenge

## Descripción

Este proyecto resuelve el desafío de cargar tiempos de viaje entre ubicaciones desde un archivo CSV y calcular la ruta más rápida entre dos ubicaciones, soportando multiempresa y autenticación (deshabilitada en modo demo). Incluye backend en Java Spring Boot y frontend en Vue 3, ambos dockerizados y listos para producción o pruebas locales.

---

## Estructura del Proyecto

```
Octadot-test/
├── backend/        # Backend Java Spring Boot
├── frontend/       # Frontend Vue 3 + Vite
├── docker-compose.yml
├── README.md       # Este archivo
```

---

## Requisitos
- Docker y Docker Compose
- (Opcional) Java 21+ y Maven para desarrollo local backend
- (Opcional) Node.js 20+ y npm para desarrollo local frontend

---

## Levantar TODO con Docker Compose

1. Desde la raíz del proyecto:
   ```bash
   docker-compose up --build -d
   ```
2. Accede a:
   - **Frontend:** [http://localhost:8081](http://localhost:8081)
   - **Backend:** [http://localhost:8080](http://localhost:8080)
3. Para detener:
   ```bash
   docker-compose down
   ```

---

## Levantar solo un servicio
- **Backend:**
  ```bash
  docker-compose up --build -d backend
  ```
- **Frontend:**
  ```bash
  docker-compose up --build -d frontend
  ```

---

## Desarrollo local (opcional)
- **Backend:**
  ```bash
  cd backend
  mvn spring-boot:run
  # http://localhost:8080
  ```
- **Frontend:**
  ```bash
  cd frontend
  npm install
  npm run dev
  # http://localhost:5173
  ```

---

## Formato del CSV

Archivo separado por punto y coma (`;`):
```
origen;destino;tiempo
A;B;10
B;C;15
C;D;20
```

---

## Endpoints principales

### Cargar conexiones (CSV)
- **POST** `/api/connections/upload`
- Formato: `multipart/form-data` con campo `file`

### Calcular ruta más corta
- **GET** `/api/routes/shortest?from=ORIGEN&to=DESTINO`
- Respuesta:
  ```json
  { "route": ["A", "B", "C"], "totalTime": 25 }
  ```

### Listar conexiones
- **GET** `/api/connections/list`

---

## Pruebas y troubleshooting

- Si tienes errores de CORS, asegúrate de que el backend permite los orígenes `http://localhost:8081` y `http://localhost:5173`.
- Si tienes error 405 o 500, revisa los logs del backend y frontend:
  ```bash
  docker-compose logs backend
  docker-compose logs frontend
  ```
- Si cambias código, ejecuta:
  ```bash
  mvn clean package -DskipTests
  docker-compose build backend
  docker-compose restart backend
  ```

---

## Testing
- El backend incluye pruebas unitarias y de integración (ver carpeta `backend/src/test`).
- Para correr tests backend:
  ```bash
  cd backend
  mvn test
  ```

---

## Notas finales
- El sistema está listo para producción o pruebas locales.
- Puedes extender la solución para autenticación JWT, multiempresa real, o persistencia en base de datos.
- Para dudas o mejoras, revisa los README de cada subcarpeta.

---