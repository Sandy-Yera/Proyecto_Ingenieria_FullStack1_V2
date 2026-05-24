# ========================================================
# Etapa 1: Descarga de dependencias (Cacheable)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS deps
WORKDIR /app

# Copiamos ÚNICAMENTE el pom.xml para congelar las dependencias en la caché
COPY pom.xml .

# Descargamos las dependencias en modo offline
RUN mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false

# ========================================================
# Etapa 2: Compilación y empaquetado (Builder)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Reutilizamos las dependencias pre-descargadas de la etapa anterior
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app /app

# Copiamos el código fuente
COPY src ./src

# Compilamos el JAR final
RUN mvn package -DskipTests -B

# ========================================================
# Etapa 3: Imagen de ejecución ultra-ligera y segura (Runner)
# ========================================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# CORRECCIÓN: Copiamos usando comodín (*.jar) para desacoplarlo de la versión del pom.xml
COPY --from=builder /app/target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario seguro
RUN chown -R brm:brm /app

# Cambiamos al contexto no-root
USER brm

# Exponemos el puerto interno asignado a buildings
EXPOSE 8084

# Comando seguro de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]