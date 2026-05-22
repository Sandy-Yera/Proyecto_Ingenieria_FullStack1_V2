#!/bin/bash

# Nombre: compile-only.sh
# Objetivo: Compilar uno o varios servicios específicos y resetear sus entornos de forma aislada.

# Colores para la consola
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

# 1. Limpieza preventiva de formatos (CRLF -> LF)
sed -i 's/\r$//' build-project.sh 2>/dev/null || true
sed -i 's/\r$//' compile-only.sh 2>/dev/null || true
sed -i 's/\r$//' compile-all.sh 2>/dev/null || true

echo -e "${YELLOW}🔑 Asegurando permisos de ejecución...${NC}"
chmod +x compile-all.sh

# 2. Configuración de caché
MAVEN_CACHE_DIR="$HOME/.m2"
mkdir -p "$MAVEN_CACHE_DIR"

# --- DETECCIÓN DE MODO (GENERAL VS SELECCIONADO) ---
if [ $# -gt 0 ]; then
    echo -e "${YELLOW}🎯 Modo selectivo activo. Procesando servicios: $@${NC}"
else
    echo -e "${GREEN}⚡ Modo general activo. Se encenderá todo el ecosistema de forma directa.${NC}"
fi

# 3. Ejecución de la compilación en contenedor Alpine de Maven
MSYS_NO_PATHCONV=1 docker run --rm -it \
    -v "$(pwd)":/app \
    -v "$MAVEN_CACHE_DIR":/root/.m2 \
    -w /app \
    maven:3.9.6-eclipse-temurin-21-alpine \
    sh compile-all.sh "$@"

COMPILE_STATUS=$?

# 4. Procesamiento post-compilación (Limpieza de BD y Despliegue)
if [ $COMPILE_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ Compilación/Validación base exitosa.${NC}"
    
    COMPOSE_SERVICES=""
    NEED_DATABASE_PAUSE=false

    # 🟢 BUCLE MÁGICO: Iteramos sobre cada uno de los argumentos proporcionados
    if [ $# -gt 0 ]; then
        for ARG in "$@"; do
            CLEAN_NAME=$(basename "$ARG")
            
            # Saltamos componentes de infraestructura para la base de datos
            if [[ "$CLEAN_NAME" != *"server"* && "$CLEAN_NAME" != *"gateway"* ]]; then
                
                # Eliminamos el prefijo 'ms-' si se incluyó en la ruta para la DB
                DB_SUFFIX=${CLEAN_NAME#ms-}
                
                # Caso especial de pluralización en base de datos
                if [ "$DB_SUFFIX" = "purchase" ]; then
                    DB_SUFFIX="purchases"
                fi
                
                DB_NAME="db_service_${DB_SUFFIX}"
                
                echo -e "${YELLOW}💥 Destruyendo y recreando base de datos para $CLEAN_NAME: $DB_NAME ...${NC}"
                
                # Ejecutamos limpieza de DB directo en el contenedor de MySQL
                docker exec -i logistica-mysql mysql -uroot -proot -e \
                    "DROP DATABASE IF EXISTS \`${DB_NAME}\`; CREATE DATABASE \`${DB_NAME}\`;"
                
                if [ $? -eq 0 ]; then
                    echo -e "${GREEN}✨ Base de datos '${DB_NAME}' reseteada con éxito.${NC}"
                    NEED_DATABASE_PAUSE=true
                else
                    echo -e "${RED}⚠️  Advertencia: No se pudo resetear la BD para $CLEAN_NAME. Asegúrate de que mysql-db esté corriendo.${NC}"
                fi
            fi
            
            # Mapeo exacto al nombre del contenedor en el docker-compose.yml
            # Si pasaste "ms-users", mapea a "brm-ms-users". Si pasaste "infraestructure/api-gateway", mapea a "brm-api-gateway"
            if [[ "$CLEAN_NAME" == "api-gateway" || "$CLEAN_NAME" == "eureka-server" || "$CLEAN_NAME" == "config-server" ]]; then
                COMPOSE_SERVICES="$COMPOSE_SERVICES brm-${CLEAN_NAME}"
            else
                # Maneja tanto si pasas "ms-users" como si se escribe incompleto
                if [[ "$CLEAN_NAME" != brm-* ]]; then
                    if [[ "$CLEAN_NAME" != ms-* ]]; then
                        COMPOSE_SERVICES="$COMPOSE_SERVICES brm-ms-${CLEAN_NAME}"
                    else
                        COMPOSE_SERVICES="$COMPOSE_SERVICES brm-${CLEAN_NAME}"
                    fi
                else
                    COMPOSE_SERVICES="$COMPOSE_SERVICES ${CLEAN_NAME}"
                fi
            fi
        done
        
        # Pausa estratégica agrupada si se reseteó alguna base de datos
        if [ "$NEED_DATABASE_PAUSE" = true ]; then
            echo -e "${YELLOW}⏱️  Esperando 5 segundos para que MySQL asimile los cambios en el storage...${NC}"
            sleep 5
        fi
    fi

    # --- ACTUALIZACIÓN INTELIGENTE DE DOCKER COMPOSE ---
    # Nota: Ajusta la ruta del compose si se encuentra en una carpeta secundaria, por defecto usa la del directorio actual.
    if [ -n "$COMPOSE_SERVICES" ]; then
        # MODO SELECCIONADO: Levanta y RECONSTRUYE (--build) únicamente los servicios pasados por parámetro
        echo -e "${YELLOW}🔄 Reconstruyendo en Docker los contenedores seleccionados: [$COMPOSE_SERVICES ]...${NC}"
        docker compose -f docker/infra-docker/compose.yml up --build -d $COMPOSE_SERVICES
    else
        # MODO GENERAL: Si se corrió sin argumentos, solo enciende lo que ya exista de forma instantánea sin --build
        echo -e "${GREEN}⚡ Encendiendo todo el ecosistema de contenedores de forma ultra veloz...${NC}"
        docker compose -f docker/infra-docker/compose.yml up -d
    fi

    echo -e "${YELLOW}🧹 Limpiando imágenes intermedias residuales (dangling)...${NC}"
    docker image prune -f

    echo -e "${GREEN}✨ ¡Todo listo! Monitorea el clúster en: http://localhost:8761${NC}"
else
    echo -e "${RED}❌ Error en la fase de compilación. Abortando despliegue.${NC}"
    exit 1
fi