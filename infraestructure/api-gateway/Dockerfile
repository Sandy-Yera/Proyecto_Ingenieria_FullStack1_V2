# ========================================================
# Etapa 1: Descarga de dependencias (Cacheable)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS deps
WORKDIR /app

# Copiamos ÚNICAMENTE el archivo de configuración de dependencias
COPY pom.xml .

# Descargamos las dependencias en modo offline. 
# Docker cacheará este layer y SOLO se volverá a ejecutar si cambias el pom.xml.
RUN mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false

# ========================================================
# Etapa 2: Compilación y empaquetado (Builder)
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copiamos las dependencias pre-descargadas desde la etapa 'deps'
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app /app

# Ahora copiamos el código fuente. Si cambias el código, solo compilará, sin re-descargar el mundo.
COPY src ./src

# Compilamos el proyecto omitiendo los tests para velocidad
RUN mvn package -DskipTests -B

# ========================================================
# Etapa 3: Imagen de ejecución ultra-ligera y segura (Runner)
# ========================================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Hardening de Seguridad: Creamos un usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR final generado en la etapa anterior de forma limpia
COPY --from=builder /app/target/*.jar app.jar

# Cambiamos la propiedad del directorio al usuario 'brm'
RUN chown -R brm:brm /app

# Cambiamos al contexto del usuario no-root
USER brm

# Exponemos el puerto interno del contenedor
EXPOSE 8080

# Comando de ejecución seguro
ENTRYPOINT ["java", "-jar", "app.jar"]