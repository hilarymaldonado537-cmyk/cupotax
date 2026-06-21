# Usar imagen con Maven y JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir el proyecto
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------
# Segunda etapa: imagen ligera para ejecutar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
