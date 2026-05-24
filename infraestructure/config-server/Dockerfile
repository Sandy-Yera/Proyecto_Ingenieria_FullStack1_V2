# ========================================================
# Etapa 1: Descarga de dependencias (Cacheable)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS deps
WORKDIR /app

# Copiamos ÚNICAMENTE el archivo de configuración de dependencias
COPY pom.xml .

# Descargamos las dependencias en modo offline.
# Quedarán grabadas en la caché de Docker de forma permanente hasta que toques el pom.xml
RUN mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false

# ========================================================
# Etapa 2: Compilación y empaquetado (Builder)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Traemos la caché de dependencias de la etapa anterior
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app /app

# Copiamos el código fuente de configuración
COPY src ./src

# Compilamos el JAR omitiendo los tests
RUN mvn package -DskipTests -B

# ========================================================
# Etapa 3: Imagen de ejecución ultra-ligera y segura (Runner)
# ========================================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR final empaquetado de forma limpia
COPY --from=builder /app/target/*.jar app.jar

# NOTA OPERATIVA: Como el config-server mapea un volumen externo para leer el config-repo 
# (/app/config-repo), aseguramos los permisos correctos para el usuario no-root
RUN mkdir -p /app/config-repo && chown -R brm:brm /app

# Cambiamos al contexto seguro no-root
USER brm

# Exponemos el puerto interno del Config Server
EXPOSE 8888

# Comando de ejecución limpio (Alpine ya trae wget para el healthcheck del compose, no requiere curl)
ENTRYPOINT ["java", "-jar", "app.jar"]