# Usamos la imagen base ultra-ligera de Java 21 sobre Alpine
FROM eclipse-temurin:21-jre-alpine

# Definimos el directorio de trabajo canónico
WORKDIR /app

# Hardening de Seguridad: Creamos el usuario de sistema 'brm' sin privilegios
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR pre-compilado (gracias a tu script compile-only.sh)
COPY target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario seguro
RUN chown -R brm:brm /app

# Cambiamos al contexto no-root
USER brm

# Exponemos el puerto interno real del servicio de autenticación
EXPOSE 8081

# Comando seguro de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]