# Usamos la imagen base ultra-ligera de Java 21 sobre Alpine
FROM eclipse-temurin:21-jre-alpine

# CORRECCIÓN: Definimos el directorio de trabajo explícito para evitar bugs de rutas
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR pre-compilado directamente dentro de /app
COPY target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario seguro
RUN chown -R brm:brm /app

# Cambiamos al contexto no-root
USER brm

# Exponemos el puerto interno real del servicio de agendamiento
EXPOSE 8098

# Comando seguro de ejecución apuntando a la ruta relativa correcta del WORKDIR
ENTRYPOINT ["java", "-jar", "app.jar"]