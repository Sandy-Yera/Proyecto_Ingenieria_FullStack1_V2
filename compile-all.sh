#!/bin/sh

# Cada vez que lo quieran usar en una pc nueva, darle permisos:
# chmod +x compile-all.sh **(se automatizó con build.sh)

# Colores para que la consola se vea pro
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Lista de rutas (Se agregan separadas por espacio)
# Adaptado para ser compatible con el entorno Docker (sh/dash)
SERVICES="
infraestructure/eureka-server
infraestructure/config-server 
infraestructure/api-gateway
services/ms-auth
services/ms-logs
services/ms-users
services/ms-buildings
services/ms-staff
services/ms-fleet
services/ms-security
services/ms-quotes
services/ms-inventory
services/ms-payments
services/ms-billing
services/ms-logistics
services/ms-notifications
services/ms-workorders
services/ms-schedule
services/ms-purchase
services/ms-price-engine
"

# --- DETECCIÓN DINÁMICA DE ARGUMENTOS (SOPORTE MULTI-SERVICIO POSIX) ---
if [ -n "$1" ]; then
    MATCHED_SERVICES=""
    
    # Recorremos cada uno de los argumentos recibidos ($@)
    for ARG in "$@"; do
        for CURRENT in $SERVICES; do
            case "$CURRENT" in
                *"$ARG"*) 
                    # Verificación de duplicados compatible con 'sh' puro sin usar grep
                    ALREADY_ADDED=false
                    for MATCH in $MATCHED_SERVICES; do
                        if [ "$MATCH" = "$CURRENT" ]; then
                            ALREADY_ADDED=true
                        fi
                    done
                    
                    if [ "$ALREADY_ADDED" = false ]; then
                        MATCHED_SERVICES="$MATCHED_SERVICES $CURRENT"
                    fi
                    ;;
            esac
        done
    done

    if [ -n "$MATCHED_SERVICES" ]; then
        SERVICES="$MATCHED_SERVICES"
        printf "${YELLOW}🎯 Modo aislado activo: Se compilarán únicamente [ $SERVICES ]${NC}\n"
    else
        printf "${RED}❌ Error: Ninguno de los argumentos ingresados coincide con una ruta válida.${NC}\n"
        exit 1
    fi
else
    printf "${GREEN}Iniciación de compilación masiva de microservicios (Limpieza y Empaquetado)...${NC}\n"
fi
# ------------------------------------------------------------------

# 📊 CÁLCULO DEL TOTAL DE SERVICIOS A COMPILAR
TOTAL_SERVICIOS=0
for S in $SERVICES; do
    TOTAL_SERVICIOS=$((TOTAL_SERVICIOS + 1))
done

# Inicializamos el contador de ejecución
CONTADOR_ACTUAL=0

for SERVICE in $SERVICES; do
    CONTADOR_ACTUAL=$((CONTADOR_ACTUAL + 1))
    
    printf "${GREEN}----------------------------------------------------${NC}\n"
    printf "${GREEN}Compilando [${CONTADOR_ACTUAL}/${TOTAL_SERVICIOS}]: $SERVICE...${NC}\n"
    printf "${GREEN}----------------------------------------------------${NC}\n"
    
    if [ -d "$SERVICE" ]; then
        cd "$SERVICE" || exit
        
        # mvn clean: Borra la carpeta /target
        # package: Genera el nuevo .jar
        # -DskipTests: Salta los tests para velocidad
        if mvn clean package -DskipTests; then
            printf "${GREEN}✅ Éxito en $SERVICE [${CONTADOR_ACTUAL}/${TOTAL_SERVICIOS}]${NC}\n"
        else
            printf "${RED}❌ Error compilando $SERVICE. Abortando.${NC}\n"
            exit 1
        fi
        
        cd - > /dev/null
    else
        printf "${YELLOW}⚠️  Advertencia: El directorio $SERVICE no existe. Saltando...${NC}\n"
    fi
done

printf "${GREEN}----------------------------------------------------${NC}\n"
printf "${GREEN}¡Todos los servicios requeridos han sido compilados con éxito!${NC}\n"
printf "${GREEN}Ya puedes ejecutar tus herramientas de despliegue.${NC}\n"
printf "${GREEN}----------------------------------------------------${NC}\n"