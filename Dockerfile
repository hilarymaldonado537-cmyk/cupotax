FROM eclipse-temurin:17-jdk-alpine

# Instalar Maven en el contenedor
RUN apk add --no-cache maven

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/CupoTax-1.0-SNAPSHOT.jar"]
