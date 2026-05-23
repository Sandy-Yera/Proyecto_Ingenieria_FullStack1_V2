#!/bin/bash

# Nombre: down-project.sh
# Objetivo: Apagar el ecosistema de forma segura, selectiva o destructiva.
# 4 modos de uso:
#   1. General estándar:  ./down-project.sh           (Apaga todo, conserva volúmenes de datos)
#   2. General destructivo:./down-project.sh --hard   (Arrasa todo el entorno y borra todos los volúmenes)
#   3. Específico estándar:./down-project.sh ms-users (Apaga servicios aislados, conserva sus datos)
#   4. Específico destructivo: ./down-project.sh kafka --hard (Apaga servicios aislados y destruye ÚNICAMENTE sus volúmenes)

# Colores para la consola
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

# Limpieza preventiva de formatos (CRLF -> LF)
sed -i 's/\r$//' down-project.sh 2>/dev/null || true

COMPOSE_FILE="docker/infra-docker/compose.yml"
HARD_RESET=false
COMPOSE_SERVICES=""

# --- DETECCIÓN DE FLAGS Y ARGUMENTOS ---
for ARG in "$@"; do
    if [ "$ARG" = "--hard" ] || [ "$ARG" = "-v" ]; then
        HARD_RESET=true
    else
        CLEAN_NAME=$(basename "$ARG")
        
        # Mapeo idéntico de nombres al ecosistema Docker de tu arquitectura
        if [[ "$CLEAN_NAME" == "api-gateway" || "$CLEAN_NAME" == "eureka-server" || "$CLEAN_NAME" == "config-server" || "$CLEAN_NAME" == "zookeeper" || "$CLEAN_NAME" == "kafka" || "$CLEAN_NAME" == "mysql-db" || "$CLEAN_NAME" == "adminer" ]]; then
            # Si el usuario escribe brm- antes, lo dejamos quieto, si no, lo agregamos
            if [[ "$CLEAN_NAME" == brm-* || "$CLEAN_NAME" == logistica-* ]]; then
                COMPOSE_SERVICES="$COMPOSE_SERVICES ${CLEAN_NAME}"
            else
                if [ "$CLEAN_NAME" = "mysql-db" ]; then
                    COMPOSE_SERVICES="$COMPOSE_SERVICES logistica-mysql"
                else
                    COMPOSE_SERVICES="$COMPOSE_SERVICES brm-${CLEAN_NAME}"
                fi
            fi
        else
            # Lógica para microservicios del negocio
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
    fi
done

# --- EJECUCIÓN DEL APAGADO ---

if [ -n "$COMPOSE_SERVICES" ]; then
    if [ "$HARD_RESET" = true ]; then
        echo -e "${RED}⚠️  Modo selectivo DESTRUCTIVO activo. Deteniendo y borrando volúmenes de: [$COMPOSE_SERVICES ]...${NC}"
        # -v aquí remueve los volúmenes asociados exclusivamente a los servicios pasados por parámetro
        docker compose -f $COMPOSE_FILE rm -f -s -v $COMPOSE_SERVICES
    else
        echo -e "${YELLOW}🎯 Modo selectivo estándar activo. Deteniendo de forma aislada: [$COMPOSE_SERVICES ]...${NC}"
        docker compose -f $COMPOSE_FILE rm -f -s $COMPOSE_SERVICES
    fi
    echo -e "${GREEN}✅ Servicios seleccionados procesados con éxito.${NC}"

else
    # Si no se especificaron servicios, la acción se ejecuta sobre todo el docker-compose
    if [ "$HARD_RESET" = true ]; then
        echo -e "${RED}⚠️  MODO HARD RESET: Deteniendo ecosistema completo y destruyendo todos los volúmenes persistentes...${NC}"
        docker compose -f $COMPOSE_FILE down -v
        echo -e "${GREEN}✅ Todo el entorno ha sido destruido por completo de forma segura.${NC}"
    else
        echo -e "${YELLOW}🛑 Modo general activo. Apagando todo el ecosistema de contenedores de forma segura...${NC}"
        docker compose -f $COMPOSE_FILE down
        echo -e "${GREEN}✅ Todos los contenedores detenidos. Datos de base de datos preservados en volumen.${NC}"
    fi
fi