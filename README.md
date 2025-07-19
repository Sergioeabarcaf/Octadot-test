# Route Optimizer - Technical Challenge

## 📋 Descripción

Este proyecto resuelve el desafío de cargar tiempos de viaje entre ubicaciones desde un archivo CSV y calcular la ruta más rápida entre dos ubicaciones. Incluye backend en Java Spring Boot y frontend en Vue 3, ambos dockerizados y listos para producción o pruebas locales.

### ✨ Características Principales

- **📤 Carga de archivos CSV** con drag & drop
- **🧮 Cálculo de rutas más cortas** usando algoritmo de Dijkstra
- **🎨 Interfaz responsive** con CSS moderno
- **🐳 Dockerizado** para fácil despliegue
- **🚀 Desplegado en servicios gratuitos** (Render + Vercel)
- **🔄 CI/CD automático** con GitHub Actions

---

## 🏗️ Arquitectura del Proyecto

### Backend (Java Spring Boot)
- **Java 21** + Spring Boot 3.2.6
- **REST API** con endpoints para carga de CSV y cálculo de rutas
- **Algoritmo de Dijkstra** para encontrar rutas más cortas
- **CORS configurado** para frontend
- **Docker multi-stage build** para despliegue en PaaS

### Frontend (Vue 3 + Vite)
- **Vue 3** con Composition API
- **Vite** para build rápido
- **Axios** para peticiones HTTP
- **CSS moderno** sin frameworks
- **Drag & drop** nativo para archivos CSV
- **Responsive design** para móviles y desktop

---

## 🚀 Despliegue Público

### Backend (Render.com)
- **URL:** https://octadot-test.onrender.com
- **Configuración:** Docker multi-stage build
- **Variables de entorno:** Configuradas automáticamente

### Frontend (Vercel)
- **URL:** https://octadot-test.vercel.app
- **Configuración:** Build automático desde GitHub
- **Variables de entorno:** `VITE_API_URL` apunta al backend

---

## 📁 Estructura del Proyecto

```
Octadot-test/
├── backend/                    # Backend Java Spring Boot
│   ├── src/
│   │   ├── main/java/com/ruta/api/
│   │   │   ├── controller/     # Controladores REST
│   │   │   ├── service/        # Lógica de negocio
│   │   │   ├── model/          # Modelos de datos
│   │   │   └── config/         # Configuración (CORS, etc.)
│   │   └── test/               # Tests unitarios e integración
│   ├── Dockerfile              # Multi-stage build
│   └── pom.xml                 # Dependencias Maven
├── frontend/                   # Frontend Vue 3
│   ├── src/
│   │   ├── components/         # Componentes Vue
│   │   │   ├── FileUploadPanel.vue
│   │   │   └── RouteCalculatorPanel.vue
│   │   └── App.vue            # Componente principal
│   ├── Dockerfile             # Build para producción
│   ├── nginx.conf             # Configuración proxy
│   └── package.json           # Dependencias npm
├── .github/workflows/         # CI/CD con GitHub Actions
│   ├── frontend.yml           # CI para frontend
│   └── backend.yml            # CI para backend
├── docker-compose.yml         # Orquestación local
└── README.md                  # Este archivo
```

---

## 🛠️ Instalación y Desarrollo Local

### Prerrequisitos
- **Java 21** (JDK)
- **Node.js 18+** y npm
- **Docker** y Docker Compose (opcional)

### Opción 1: Desarrollo con Docker Compose (Recomendado)

```bash
# Clonar el repositorio
git clone https://github.com/Sergioeabarcaf/Octadot-test.git
cd Octadot-test

# Ejecutar con Docker Compose
docker-compose up --build

# Acceder a la aplicación
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
```

### Opción 2: Desarrollo Local

#### Backend
```bash
cd backend
./mvnw spring-boot:run
# Backend disponible en http://localhost:8080
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
# Frontend disponible en http://localhost:5173
```

---

## 📊 API Endpoints

### Backend (Base URL: `http://localhost:8080`)

#### Cargar archivo CSV
```http
POST /api/connections/upload
Content-Type: multipart/form-data

file: [archivo CSV]
```

#### Calcular ruta más corta
```http
GET /api/routes/shortest?from=A&to=B
```

#### Listar conexiones disponibles
```http
GET /api/connections/list
```

### Formato CSV Esperado
```csv
A,B,10
B,C,15
A,C,25
```

---

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm run test
```

---

## 🐳 Docker

### Backend Dockerfile
```dockerfile
# Multi-stage build para optimizar tamaño
FROM maven:3.9.4-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Frontend Dockerfile
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

#### Frontend CI
- **Trigger:** Push a `main` o PR
- **Actions:** Instalar dependencias, lint, build, test
- **Deploy:** Automático a Vercel

#### Backend CI
- **Trigger:** Push a `main` o PR
- **Actions:** Build con Maven, tests
- **Deploy:** Manual en Render (webhook configurado)

---

## 🌐 Configuración de Producción

### Variables de Entorno

#### Frontend (.env.production)
```env
VITE_API_URL=https://octadot-test.onrender.com/api
```

#### Backend (Render Environment Variables)
```env
SPRING_PROFILES_ACTIVE=production
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://octadot-test.vercel.app"
        ));
        // ... más configuración
    }
}
```

---

## 🚨 Troubleshooting

### Problemas Comunes

#### CORS Errors
- Verificar que el backend permita el origen del frontend
- Revisar configuración en `CorsConfig.java`

#### 404 en peticiones de rutas
- Asegurar que `VITE_API_URL` esté configurado correctamente
- Verificar que el backend esté desplegado y funcionando

#### Tests fallando
- Los tests están temporalmente deshabilitados por incompatibilidad Java 24
- Para desarrollo local, usar Java 21

#### Deploy fallando en Render
- Verificar que el Dockerfile use multi-stage build
- Asegurar que no haya dependencias de archivos precompilados

---

## 📈 Próximas Mejoras

### Alta Prioridad
- [ ] Implementar base de datos PostgreSQL
- [ ] Agregar autenticación JWT
- [ ] Implementar caching con Redis

### Media Prioridad
- [ ] Agregar mapas interactivos
- [ ] Implementar múltiples algoritmos de rutas
- [ ] Mejorar UI/UX con animaciones
- [ ] Agregar tests de integración

### Baja Prioridad
- [ ] Analytics y métricas
- [ ] Exportación de rutas a PDF
- [ ] Modo offline
- [ ] PWA capabilities

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 👨‍💻 Autor

**Sergio Abarca**
- GitHub: [@Sergioeabarcaf](https://github.com/Sergioeabarcaf)
- LinkedIn: [Sergio Abarca](https://www.linkedin.com/in/sergioeabarcaf)
