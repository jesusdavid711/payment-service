# Payment Management Microservice

Microservicio de gestión de pagos construido con **Quarkus**, siguiendo una **arquitectura hexagonal** (puertos y adaptadores).

## 🚀 Cómo correr el proyecto

### Requisitos
- Java 21
- Docker y Docker Compose
- Maven (incluido `./mvnw`)

### 1. Levantar la Infraestructura (Base de Datos)
El proyecto incluye un `docker-compose.yml` para facilitar la ejecución.
```bash
docker-compose up -d
```
Esto levantará:
- **PostgreSQL**: Puerto `5434` (para no entrar en conflicto con instalaciones locales en el 5432).
- **Adminer (opcional)**: Interfaz web para DB en el puerto `8080`.

### 2. Ejecutar la Aplicación
```bash
./mvnw quarkus:dev
```

## ⚙️ Variables de Entorno y Configuración
Puedes configurar la aplicación a través de `src/main/resources/application.properties` o variables de entorno:

| Propiedad | Variable de Entorno | Valor por Defecto |
|-----------|----------------------|-------------------|
| URL JDBC | `QUARKUS_DATASOURCE_JDBC_URL` | `jdbc:postgresql://localhost:5434/payments` |
| Usuario DB| `QUARKUS_DATASOURCE_USERNAME` | `postgres` |
| Clave DB  | `QUARKUS_DATASOURCE_PASSWORD` | `secret` |

## 🛠️ Arquitectura
El proyecto utiliza **Hexagonal Architecture**:
- **Domain**: Entidades, Enums y lógica pura de negocio (transiciones de estado).
- **Application**: Puertos (interfaces) y servicios que coordinan los casos de uso.
- **Infrastructure**: Adaptadores REST (controllers) y persistencia (Panache con Flyway).

## 📖 Documentación de la API (Swagger)
Una vez iniciada la app, accede a:
👉 [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

## 📡 Ejemplos de Pruebas (curl)

### 1. Registrar un Pago
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "PAY-100",
    "customerId": "CUST-001",
    "amount": 25000.00,
    "currency": "COP",
    "method": "CARD"
  }'
```

### 2. Consultar Pago por ID
```bash
curl http://localhost:8080/api/payments/1
```

### 3. Consultar con Filtros y Paginación
```bash
curl "http://localhost:8080/api/payments?status=PENDING&page=0&size=5"
```

### 4. Cambiar Estado (Ej: Aprobar)
```bash
curl -X PATCH http://localhost:8080/api/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}'
```

## 🧪 Pruebas
Ejecutar todos los tests (Unitarios e Integración):
```bash
./mvnw test
```
