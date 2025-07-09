# Usar una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine AS builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar las dependencias (esto se cachea si no cambia el pom.xml)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Crear una nueva imagen más ligera para la ejecución
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear un usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la imagen builder
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Configurar variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker

# Punto de entrada de la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
