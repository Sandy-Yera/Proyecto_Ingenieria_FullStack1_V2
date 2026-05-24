# ==========================================
# ETAPA ÚNICA: Runtime Seguro y Limpio (Runner)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creamos usuario y grupo sin privilegios por seguridad
RUN addgroup -S brm && adduser -S brm -G brm

# Copiamos el JAR directamente desde la carpeta target y lo renombramos
COPY target/*.jar app.jar

# Asignamos los permisos correspondientes al usuario brm
RUN chown -R brm:brm /app

# Cambiamos al usuario sin privilegios
USER brm

# Exponemos el puerto (Asegúrate de cambiar esto en los otros microservicios)
EXPOSE 8082

# Arrancamos usando el archivo JAR directamente, sin el flag de CDS
ENTRYPOINT ["java", "-jar", "app.jar"]