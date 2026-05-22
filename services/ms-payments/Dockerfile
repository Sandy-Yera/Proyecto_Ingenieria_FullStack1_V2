# Usamos la imagen base ultra-ligera de Java 21 sobre Alpine
FROM eclipse-temurin:21-jre-alpine

# CORRECCIÓN: Definimos el directorio de trabajo explícito para evitar bugs de rutas
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR pre-compilado directamente dentro de nuestro WORKDIR (/app/app.jar)
COPY target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario seguro
RUN chown -R brm:brm /app

# Cambiamos al contexto no-root de forma segura
USER brm

# Exponemos el puerto interno real del servicio de pagos
EXPOSE 8091

# Comando seguro de ejecución apuntando a la ruta relativa correcta del WORKDIR
ENTRYPOINT ["java", "-jar", "app.jar"]