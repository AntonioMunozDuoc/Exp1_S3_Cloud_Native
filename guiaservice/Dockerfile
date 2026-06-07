# Etapa 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos al Maven Wrapper
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Comentamos el usuario spring temporalmente para evitar 
# errores de permisos al escribir en el disco EFS montado desde AWS
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# IMPORTANTE -> copiar wallet de Oracle
COPY wallet wallet

# IMPORTANTE -> Crear la carpeta donde se montará el disco EFS físico
RUN mkdir -p /mnt/efs

# Exponemos el puerto de tu aplicación
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]