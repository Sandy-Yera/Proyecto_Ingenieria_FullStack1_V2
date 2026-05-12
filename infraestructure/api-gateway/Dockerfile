# Etapa 1: Compilación
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
# Usamos el wrapper de maven que viene en el proyecto para compilar
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Imagen ligera)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]