# Usar imagen con Maven y JDK 11
FROM maven:3.8.4-openjdk-11-slim AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir el proyecto (saltando tests)
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------
# Segunda etapa: imagen ligera para ejecutar
FROM openjdk:11-jre-slim

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
