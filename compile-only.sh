#!/bin/bash

# Nombre: compile-only.sh
# Objetivo: Compilar uno o varios servicios específicos y resetear sus entornos de forma aislada.

# 1. Limpieza de formatos (CRLF -> LF)
sed -i 's/\r$//' build-project.sh
sed -i 's/\r$//' compile-only.sh
sed -i 's/\r$//' compile-all.sh

echo "Asegurando permisos de ejecución..."
chmod +x compile-all.sh

# 2. Configuración de caché
MAVEN_CACHE_DIR="$HOME/.m2"
mkdir -p "$MAVEN_CACHE_DIR"

# --- DETECCIÓN DE MODO (GENERAL VS SELECCIONADO) ---
if [ $# -gt 0 ]; then
    echo "🎯 Modo selectivo activo. Procesando servicios: $@"
else
    echo "⚡ Modo general activo. Se encenderá todo el ecosistema sin recompilar."
fi

# 3. Ejecución de la compilación en Docker
# 🟢 CAMBIO CRÍTICO: Pasamos "$@" para enviarle TODOS los argumentos recibidos a compile-all.sh
MSYS_NO_PATHCONV=1 docker run --rm -it \
    -v "$(pwd)":/app \
    -v "$MAVEN_CACHE_DIR":/root/.m2 \
    -w /app \
    maven:3.9.6-eclipse-temurin-21 \
    sh compile-all.sh "$@"

COMPILE_STATUS=$?

# 4. Procesamiento post-compilación (Limpieza de BD y Despliegue)
if [ $COMPILE_STATUS -eq 0 ]; then
    echo "✅ Compilación exitosa."
    
    # Lista donde acumularemos los nombres de los servicios para Docker Compose
    COMPOSE_SERVICES=""
    NEED_DATABASE_PAUSE=false

    # 🟢 BUCLE MÁGICO: Iteramos sobre cada uno de los argumentos proporcionados
    if [ $# -gt 0 ]; then
        for ARG in "$@"; do
            CLEAN_NAME=$(basename "$ARG")
            
            # Saltamos componentes de infraestructura para la base de datos
            if [[ "$CLEAN_NAME" != *"server"* && "$CLEAN_NAME" != *"gateway"* ]]; then
                
                # Eliminamos el prefijo 'ms-' (ej: ms-users -> users)
                DB_SUFFIX=${CLEAN_NAME#ms-}
                
                # Caso especial de pluralización en base de datos
                if [ "$DB_SUFFIX" = "purchase" ]; then
                    DB_SUFFIX="purchases"
                fi
                
                DB_NAME="db_service_${DB_SUFFIX}"
                
                echo "💥 Destruyendo y recreando base de datos para $CLEAN_NAME: $DB_NAME ..."
                
                docker exec -i logistica-mysql mysql -uroot -proot -e \
                    "DROP DATABASE IF EXISTS \`${DB_NAME}\`; CREATE DATABASE \`${DB_NAME}\`;"
                
                if [ $? -eq 0 ]; then
                    echo "✨ Base de datos '${DB_NAME}' reseteada con éxito."
                    NEED_DATABASE_PAUSE=true
                else
                    echo "⚠️  Advertencia: No se pudo resetear la BD para $CLEAN_NAME."
                fi
            fi
            
            # Acumulamos el nombre del contenedor correspondiente para usarlo en el Docker Compose final
            COMPOSE_SERVICES="$COMPOSE_SERVICES brm-${CLEAN_NAME}"
        done
        
        # Pausa estratégica agrupada si se reseteó alguna base de datos
        if [ "$NEED_DATABASE_PAUSE" = true ]; then
            echo "⏱️  ESPERANDOOOOO 5 segundos para que MySQL asimile los cambios..."
            sleep 5
        fi
    fi

    # --- ACTUALIZACIÓN INTELIGENTE DE DOCKER COMPOSE ---
    if [ -n "$COMPOSE_SERVICES" ]; then
        # MODO SELECCIONADO: Levanta y RECONSTRUYE (--build) únicamente los servicios pasados por parámetro
        echo "🔄 Reconstruyendo en Docker los contenedores seleccionados: [$COMPOSE_SERVICES ]..."
        docker-compose -f docker/infra-docker/compose.yml up --build -d $COMPOSE_SERVICES
    else
        # MODO GENERAL: Si se corrió sin argumentos, solo enciende lo que ya exista de forma instantánea sin --build
        echo "⚡ Encendiendo todo el ecosistema de contenedores de forma ultra veloz..."
        docker-compose -f docker/infra-docker/compose.yml up -d
    fi

    echo "🧹 Limpiando imágenes residuales del contenedor actualizado..."
    docker image prune -f

    echo "✨ ¡Listo! Verifica en http://localhost:8761"
else
    echo "❌ Error en la compilación."
    exit 1
fi