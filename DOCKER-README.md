# 🐳 Containerización Docker - Company API

Este proyecto incluye una configuración completa de Docker para ejecutar la aplicación Spring Boot junto con una base de datos PostgreSQL.

## 📋 Requisitos Previos

- Docker v20.0+ instalado
- Docker Compose v2.0+ instalado  
- Puertos 8080 y 5432 disponibles
- Mínimo 1GB RAM disponible para contenedores

## 🚀 Inicio Rápido

```bash
# 1. Construir y levantar todos los servicios
docker-compose up --build -d

# 2. Verificar que todo esté funcionando
docker-compose ps

# 3. Ver logs si es necesario
docker-compose logs -f

# 4. Acceder a la aplicación
# Backend: http://localhost:8080
```

## 🛑 Detener la Aplicación

```bash
# Parar servicios
docker-compose down

# Parar servicios y remover volúmenes (elimina datos)
docker-compose down -v

# Parar servicios y remover todo (incluyendo imágenes)
docker-compose down --rmi all -v
```

## 🌐 URLs y Accesos

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Backend API** | http://localhost:8080 | - |
| **PostgreSQL** | localhost:5432 | jcuadrado / jcuadrado |

## 🐘 Configuración de PostgreSQL

- **Base de datos**: `admin_portal`
- **Usuario**: `jcuadrado`
- **Contraseña**: `jcuadrado`
- **Puerto**: `5432`

## 📊 Servicios Incluidos

### 🔧 Backend (company-backend)
- **Imagen**: Multi-stage build con Eclipse Temurin 17 (Alpine)
- **Puerto**: 8080
- **Perfil Spring**: `docker` (configuración en application-docker.properties)
- **Herramientas incluidas**: curl para debugging
- **Características**:
  - Usuario no-root por seguridad
  - Spring Boot Actuator habilitado
  - JWT Authentication configurado
  - Conexión optimizada a PostgreSQL
  - Imagen Alpine optimizada (menor tamaño)

### 🗄️ Base de Datos (postgres-db)
- **Imagen**: `postgres:15-alpine` (optimizada)
- **Puerto**: 5432
- **Base de datos**: `admin_portal`
- **Volumen persistente**: `postgres_data`
- **Scripts de inicialización**: `./init-db/`
- **Acceso directo**: `docker-compose exec postgres-db psql -U jcuadrado -d admin_portal`

## 🛠️ Comandos Útiles

```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs de un servicio específico
docker-compose logs -f company-backend
docker-compose logs -f postgres-db

# Ejecutar comandos en un contenedor
docker-compose exec company-backend sh
docker-compose exec postgres-db psql -U jcuadrado -d admin_portal

# Verificar endpoints desde dentro del contenedor
docker-compose exec company-backend curl -f http://localhost:8080/actuator/health
docker-compose exec company-backend curl -f http://localhost:8080/actuator/info

# Reiniciar un servicio específico
docker-compose restart company-backend

# Reconstruir solo el backend
docker-compose up --build company-backend

# Ver recursos utilizados
docker stats
```

## 🔧 Configuración Personalizada

### ⚙️ Configuración Optimizada
La configuración está optimizada para evitar duplicación:
- **Variables de entorno**: Solo `SPRING_PROFILES_ACTIVE=docker` en docker-compose
- **Configuración de BD**: Centralizada en `application-docker.properties`
- **Single Source of Truth**: Una sola fuente de configuración por ambiente

### Archivo de Configuración Docker
```properties
# application-docker.properties
spring.datasource.url=jdbc:postgresql://postgres-db:5432/admin_portal
spring.datasource.username=jcuadrado
spring.datasource.password=jcuadrado
spring.jpa.hibernate.ddl-auto=update
management.endpoints.web.exposure.include=health,info
```

### Perfiles de Spring Boot
- **Desarrollo local**: `dev` → `application-dev.properties` (localhost:5432)
- **Docker**: `docker` → `application-docker.properties` (postgres-db:5432)
- **QA**: `qa` → `application-qa.properties`
- **Producción**: `prod` → `application-prod.properties`

## 🚨 Solución de Problemas

### 🔍 Diagnóstico Rápido
```bash
# Ver estado general
docker-compose ps
docker-compose logs --tail=50

# Verificar conectividad de red
docker network ls
docker network inspect company_company-network

# Verificar que los servicios respondan (desde el host)
curl -f http://localhost:8080/actuator/health  # Backend
psql -h localhost -p 5432 -U jcuadrado -d admin_portal -c "\dt"  # PostgreSQL

# Verificar recursos del sistema
docker stats --no-stream
```

### ❌ El backend no puede conectar a la base de datos
```bash
# 1. Verificar logs de PostgreSQL
docker-compose logs postgres-db

# 2. Verificar que PostgreSQL esté listo
docker-compose exec postgres-db pg_isready -U jcuadrado -d admin_portal

# 3. Reiniciar servicios en orden correcto
docker-compose down
docker-compose up postgres-db -d
# Esperar unos segundos para que PostgreSQL esté listo
sleep 10
docker-compose up company-backend -d
```

### 🔌 Puertos ocupados
```bash
# Verificar qué proceso usa los puertos
sudo lsof -i :8080
sudo lsof -i :5432

# Alternativa: usar netstat
sudo netstat -tulpn | grep -E ':(8080|5432)'

# Modificar puertos en docker-compose.yml si es necesario
```

### 🧹 Limpiar todo y empezar de nuevo
```bash
# Limpieza completa manual
docker-compose down -v
docker image prune -f
docker volume prune -f
docker rmi company-company-backend 2>/dev/null || true

# Reconstruir desde cero
docker-compose up --build -d
```

## 🎯 Casos de Uso Específicos

### 🔄 Desarrollo Activo
```bash
# Desarrollo con hot reload (solo rebuild del backend)
docker-compose up --build company-backend

# Ver logs de un servicio específico
docker-compose logs -f company-backend | grep ERROR

# Acceso directo a la base de datos
docker-compose exec postgres-db psql -U jcuadrado -d admin_portal
```

### 🧪 Testing y QA
```bash
# Ejecutar tests dentro del contenedor
docker-compose exec company-backend ./mvnw test

# Backup de la base de datos
docker-compose exec postgres-db pg_dump -U jcuadrado admin_portal > backup_$(date +%Y%m%d).sql

# Restaurar backup
docker-compose exec -T postgres-db psql -U jcuadrado -d admin_portal < backup_file.sql
```

### 📦 Deployment
```bash
# Build para producción (sin cache)
docker-compose build --no-cache

# Exportar imágenes
docker save company-company-backend:latest | gzip > company-backend.tar.gz

# Importar en otro servidor
gunzip -c company-backend.tar.gz | docker load
```

## 🔧 Personalización Avanzada

### 🛠️ Comandos de Debug Avanzados
```bash
# Entrar al contenedor del backend para debug
docker-compose exec company-backend sh

# Ver variables de entorno del contenedor
docker-compose exec company-backend env | grep -E "(SPRING|POSTGRES)"

# Verificar conectividad entre contenedores
docker-compose exec company-backend ping postgres-db

# Test de endpoints con curl desde dentro del contenedor
docker-compose exec company-backend curl -v http://localhost:8080/actuator/health
docker-compose exec company-backend curl -v http://localhost:8080/actuator/info

# Ver procesos ejecutándose en el contenedor
docker-compose exec company-backend ps aux

# Verificar logs de aplicación en tiempo real
docker-compose logs -f company-backend | grep -v "INFO"

# Verificar puerto interno del contenedor
docker-compose exec company-backend netstat -tulpn | grep 8080
```

### 🌍 Variables de Entorno Personalizadas
Crea un archivo `.env` en la raíz del proyecto:
```bash
# .env
POSTGRES_DB=mi_empresa_db
POSTGRES_USER=mi_usuario
POSTGRES_PASSWORD=mi_password_seguro
BACKEND_PORT=9090
```

### 🏗️ Profiles Personalizados
```bash
# Usar perfil personalizado
SPRING_PROFILES_ACTIVE=mi-perfil docker-compose up
```

---

**📝 Última actualización**: Julio 2025  
**🔧 Versión**: Spring Boot 3.5.3 + PostgreSQL 15 + Java 17 + curl  
**👨‍💻 Configurado para**: Desarrollo local y containerización completa
