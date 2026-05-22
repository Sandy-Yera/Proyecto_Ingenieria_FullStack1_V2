# Usamos la imagen base ultra-ligera de Java 21 sobre Alpine
FROM eclipse-temurin:21-jre-alpine

# CORRECCIÓN: Definimos el directorio de trabajo explícito y limpio
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Definimos el volumen temporal para el servidor embebido Tomcat
VOLUME /tmp

# Copiamos el JAR pre-compilado directamente dentro de nuestro WORKDIR (/app/app.jar)
COPY target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario seguro
RUN chown -R brm:brm /app

# Cambiamos al contexto no-root de forma segura
USER brm

# Exponemos el puerto interno real del servicio de cotizaciones
EXPOSE 8088

# Comando seguro de ejecución apuntando a la ruta relativa correcta dentro del WORKDIR
ENTRYPOINT ["java", "-jar", "app.jar"]