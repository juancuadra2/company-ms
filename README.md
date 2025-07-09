# API de Gestión de Empresas

Este proyecto es una API RESTful para la gestión de empresas, desarrollada con Spring Boot.

## Requisitos Previos

Para ejecutar esta aplicación necesitarás:

- Java 17 o superior
- Maven 3.6 o superior
- PostgreSQL 12 o superior

## Configuración de la Base de Datos

Por defecto, la aplicación está configurada para conectarse a una base de datos PostgreSQL:

```
URL: jdbc:postgresql://localhost:5432/admin_portal
Usuario: jcuadrado
Contraseña: jcuadrado
```

Si necesitas modificar esta configuración, puedes hacerlo en el archivo `src/main/resources/application-dev.properties`.

Alternativamente, puedes usar una base de datos H2 en memoria descomentando las líneas correspondientes en el archivo de propiedades.

### Perfiles de Configuración

La aplicación soporta múltiples perfiles:

- **dev** (por defecto): Configuración de desarrollo con PostgreSQL
- **qa**: Configuración de QA (requiere configurar variables de entorno)  
- **prod**: Configuración de producción (requiere configurar variables de entorno)
- **test**: Configuración para pruebas con H2 en memoria

Para cambiar el perfil activo, modifica la propiedad `spring.profiles.active` en `application.properties` o usa variables de entorno.

### Configuración JWT

⚠️ **Importante**: En un entorno de producción, debes cambiar la clave secreta JWT (`security.jwt.secret-key`) por una clave segura y única. El token JWT tiene una duración de 1 hora (3600000 ms) por defecto.

## Instalación y Ejecución

1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd company
   ```

2. Compila el proyecto:
   ```bash
   ./mvnw clean package
   ```

3. Ejecuta la aplicación:

   Opción 1: Usando Maven
   ```bash
   ./mvnw spring-boot:run
   ```

   Opción 2: Usando el archivo JAR generado
   ```bash
   java -jar target/company-0.0.1-SNAPSHOT.jar
   ```

La aplicación estará disponible en `http://localhost:8080`.

## Autenticación

La aplicación utiliza autenticación JWT (JSON Web Tokens). Para obtener acceso a los endpoints protegidos, primero debes autenticarte en el endpoint de login para obtener un token JWT.

### Endpoint de Autenticación

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/auth/login` | Iniciar sesión y obtener token JWT |

### Usuarios Predefinidos

1. **Administrador**
   - Usuario: `admin`
   - Contraseña: `admin`
   - Rol: `ADMIN`
   - Acceso: Todos los endpoints

2. **Usuario Básico**
   - Usuario: `user`
   - Contraseña: `user`
   - Rol: `BASIC_USER`
   - Acceso: Solo puede crear empresas (POST /companies)

## Endpoints de la API

### Autenticación (Authentication)

| Método | URL | Descripción | Acceso |
|--------|-----|-------------|--------|
| POST | `/auth/login` | Iniciar sesión y obtener token JWT | Público |

### Empresas (Companies)

| Método | URL | Descripción | Acceso |
|--------|-----|-------------|--------|
| POST | `/companies` | Crear una nueva empresa | ADMIN, BASIC_USER |
| GET | `/companies` | Obtener todas las empresas (con paginación) | ADMIN |
| GET | `/companies/{id}` | Obtener una empresa por ID | ADMIN |
| PUT | `/companies/{id}` | Actualizar una empresa | ADMIN |
| DELETE | `/companies/{id}` | Eliminar una empresa | ADMIN |

### Parámetros para GET /companies

- `search`: Término de búsqueda (opcional, por defecto: "")
- `page`: Número de página (opcional, por defecto: 0)
- `size`: Tamaño de página (opcional, por defecto: 10)

## Ejemplos de Uso

### 1. Obtener token JWT

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Crear una empresa

```bash
curl -X POST http://localhost:8080/companies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu_token_jwt>" \
  -d '{
    "name": "Empresa Ejemplo",
    "nit": "123456789",
    "address": "Calle Principal 123",
    "phone": "+34123456789"
  }'
```

### 3. Obtener todas las empresas

```bash
curl -X GET http://localhost:8080/companies \
  -H "Authorization: Bearer <tu_token_jwt>"
```

### 4. Obtener una empresa por ID

```bash
curl -X GET http://localhost:8080/companies/1 \
  -H "Authorization: Bearer <tu_token_jwt>"
```

### 5. Actualizar una empresa

```bash
curl -X PUT http://localhost:8080/companies/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu_token_jwt>" \
  -d '{
    "name": "Empresa Actualizada",
    "address": "Nueva Dirección 456",
    "phone": "+34987654321"
  }'
```

### 6. Eliminar una empresa

```bash
curl -X DELETE http://localhost:8080/companies/1 \
  -H "Authorization: Bearer <tu_token_jwt>"
```

## Esquemas de Datos

### Estructura de una Empresa (Company)

```json
{
  "id": 1,
  "name": "Empresa Ejemplo",
  "nit": "123456789",
  "address": "Calle Principal 123",
  "phone": "+34123456789"
}
```

### Crear Empresa (CreateCompanyDto)

**Campos obligatorios:**
- `name`: Nombre de la empresa (3-100 caracteres)
- `nit`: NIT de la empresa (5-20 caracteres, debe ser único)

**Campos opcionales:**
- `address`: Dirección de la empresa
- `phone`: Teléfono (formato: +[código][número], 10-15 dígitos)

### Actualizar Empresa (UpdateCompanyDto)

**Todos los campos son opcionales:**
- `name`: Nuevo nombre (3-100 caracteres)
- `nit`: Nuevo NIT (5-20 caracteres, debe ser único)
- `address`: Nueva dirección
- `phone`: Nuevo teléfono (formato: +[código][número], 10-15 dígitos)

### Respuesta Paginada

```json
{
  "data": [
    {
      "id": 1,
      "name": "Empresa 1",
      "nit": "123456789",
      "address": "Dirección 1",
      "phone": "+34123456789"
    }
  ],
  "totalPages": 1,
  "totalElements": 1,
  "pageSize": 10,
  "currentPage": 1
}
```

### Respuesta de Autenticación

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Respuesta de Error

```json
{
  "timestamp": "2025-07-09T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "details": {
    "campo": "Detalle específico del error"
  }
}
```

## Desarrollo

Este proyecto utiliza:
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Security
- JWT (JSON Web Tokens) para autenticación
- PostgreSQL / H2 Database
- Maven
- Lombok
- MapStruct
- JaCoCo (para cobertura de código)

Para ejecutar las pruebas:
```bash
./mvnw test
```

Para generar el reporte de cobertura de código:
```bash
./mvnw test jacoco:report
```

El reporte de cobertura estará disponible en `target/site/jacoco/index.html`.

## Notas Técnicas

### Funcionalidades Implementadas
- ✅ Autenticación JWT completa
- ✅ CRUD completo de empresas
- ✅ Validación de datos de entrada
- ✅ Búsqueda por nombre y NIT
- ✅ Paginación de resultados
- ✅ Manejo de errores y excepciones
- ✅ Pruebas unitarias e integración
- ✅ Cobertura de código con JaCoCo
