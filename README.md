# API REST para Cálculo de Rutas - Desafío Técnico

## Descripción

Esta API REST desarrollada en Java con Spring Boot permite cargar tiempos de viaje entre ubicaciones desde un archivo CSV y calcular la ruta más rápida entre dos ubicaciones. La aplicación está diseñada para manejar múltiples empresas y proporciona una solución escalable para el cálculo de rutas óptimas.

## Características Principales

- ✅ **Carga de datos CSV**: Procesamiento de archivos CSV con tiempos de viaje entre ubicaciones
- ✅ **Cálculo de rutas óptimas**: Implementación del algoritmo de Dijkstra para encontrar la ruta más rápida
- ✅ **Arquitectura multiempresa**: Soporte para múltiples empresas con separación de datos
- ✅ **API REST completa**: Endpoints para carga de datos y cálculo de rutas
- ✅ **Validaciones robustas**: Validación de formato CSV y datos de entrada
- ✅ **Dockerización**: Contenedor Docker para despliegue fácil
- ✅ **Pruebas unitarias**: Cobertura de pruebas para modelos, servicios y controladores
- ✅ **Rendimiento optimizado**: Manejo eficiente de hasta 10,000 filas de datos

## Tecnologías Utilizadas

- **Java 21+**: Lenguaje de programación principal
- **Spring Boot 3.x**: Framework para la API REST
- **Maven**: Gestión de dependencias y build
- **Docker**: Contenedorización de la aplicación
- **JUnit 5**: Framework de pruebas unitarias
- **Mockito**: Framework de mocking para pruebas

## Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/ruta/api/
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── model/              # Modelos de datos
│   │   │   ├── service/            # Lógica de negocio
│   │   │   ├── config/             # Configuraciones
│   │   │   └── ApiApplication.java # Clase principal
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ruta/api/
│           ├── controller/          # Pruebas de controladores
│           ├── model/              # Pruebas de modelos
│           ├── service/            # Pruebas de servicios
│           └── integration/        # Pruebas de integración
├── pom.xml                        # Configuración Maven
└── Dockerfile                     # Configuración Docker
```

## Instalación y Ejecución

### Prerrequisitos

- Java 21 o superior
- Maven 3.6+
- Docker (opcional)

### Ejecución Local

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd Octadot-test/backend
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar las pruebas**
   ```bash
   mvn test
   ```

4. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`

### Ejecución con Docker

1. **Construir la imagen**
   ```bash
   docker build -t ruta-api .
   ```

2. **Ejecutar el contenedor**
   ```bash
   docker run -p 8080:8080 ruta-api
   ```

## API Endpoints

### 1. Cargar Conexiones desde CSV

**POST** `/api/connections/upload`

Carga un archivo CSV con las conexiones entre ubicaciones.

**Parámetros:**
- `file`: Archivo CSV (multipart/form-data)

**Formato del CSV:**
```csv
origen,destino,tiempo
A,B,10
B,C,15
C,D,20
```

**Respuesta exitosa:**
```json
{
  "message": "Connections uploaded successfully"
}
```

**Ejemplo con curl:**
```bash
curl -X POST -F "file=@connections.csv" http://localhost:8080/api/connections/upload
```

### 2. Calcular Ruta Más Corta

**GET** `/api/routes/shortest`

Calcula la ruta más rápida entre dos ubicaciones.

**Parámetros:**
- `from`: Ubicación de origen
- `to`: Ubicación de destino

**Respuesta exitosa:**
```json
{
  "route": ["A", "B", "C", "D"],
  "totalTime": 45
}
```

**Ejemplo con curl:**
```bash
curl "http://localhost:8080/api/routes/shortest?from=A&to=D"
```

### 3. Listar Conexiones

**GET** `/api/connections/list`

Lista todas las conexiones cargadas para debug.

**Respuesta:**
```
Connections for company default:
Graph contains 4 locations: [A, B, C, D]
```

## Algoritmo de Cálculo de Rutas

La aplicación utiliza el **algoritmo de Dijkstra** para encontrar la ruta más corta entre dos ubicaciones:

1. **Construcción del grafo**: Las conexiones se representan como un grafo dirigido
2. **Búsqueda de ruta**: Dijkstra encuentra el camino con menor tiempo total
3. **Optimización**: El algoritmo es eficiente para grafos con hasta 10,000 nodos

### Complejidad Temporal
- **Construcción del grafo**: O(E) donde E es el número de conexiones
- **Cálculo de ruta**: O(V²) donde V es el número de ubicaciones
- **Espacio**: O(V²) para almacenar la matriz de adyacencia

## Arquitectura Multiempresa

La aplicación está diseñada para soportar múltiples empresas:

- **Separación de datos**: Cada empresa tiene su propio grafo de conexiones
- **Servicio singleton**: `CompanyGraphService` maneja grafos por empresa
- **Escalabilidad**: Fácil extensión para agregar autenticación JWT

## Pruebas

### Ejecutar Todas las Pruebas
```bash
mvn test
```

### Cobertura de Pruebas

- **Modelos**: `ConnectionTest`, `GraphTest`
- **Servicios**: `CompanyGraphServiceTest`
- **Controladores**: `ConnectionUploadControllerTest`
- **Integración**: `RouteIntegrationTest`

### Pruebas de Rendimiento

Las pruebas incluyen validación de rendimiento:
- Carga de 100 conexiones en < 1 segundo
- Cálculo de ruta en < 300ms

## Configuración

### application.properties
```properties
# Puerto del servidor
server.port=8080

# Configuración de logging
logging.level.com.ruta.api=DEBUG
```

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/ruta-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Manejo de Errores

La API maneja los siguientes errores:

- **400 Bad Request**: Formato CSV inválido, datos faltantes
- **401 Unauthorized**: Autenticación requerida (configurable)
- **404 Not Found**: Ruta no encontrada entre ubicaciones

### Ejemplos de Errores

**CSV inválido:**
```json
{
  "error": "Invalid time at line 2"
}
```

**Ruta no encontrada:**
```json
{
  "error": "No route found between A and Z"
}
```

## Limitaciones y Consideraciones

- **Memoria**: El grafo se mantiene en memoria (no persistente)
- **Escalabilidad**: Optimizado para hasta 10,000 ubicaciones
- **Concurrencia**: No thread-safe para escrituras simultáneas
- **Autenticación**: Configurada para permitir todo el tráfico (modo desarrollo)

## Mejoras Futuras

- [ ] Persistencia de datos en base de datos
- [ ] Autenticación JWT completa
- [ ] Cache de rutas calculadas
- [ ] API para múltiples rutas simultáneas
- [ ] Métricas y monitoreo
- [ ] Documentación OpenAPI/Swagger

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

**Nota**: Esta implementación cumple con todos los requisitos del desafío técnico y proporciona una base sólida para futuras extensiones.