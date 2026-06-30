#!/bin/sh

# Nombre: test-all.sh
# Objetivo: Ejecutar los tests unitarios reales (servicio/lógica de negocio) de todos los
# microservicios. Se excluyen las clases *ApplicationTests (el "contextLoads" generado por
# Spring Initializr), ya que esas requieren una base de datos viva y no son tests de lógica.

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

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

    for ARG in "$@"; do
        for CURRENT in $SERVICES; do
            case "$CURRENT" in
                *"$ARG"*)
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
        printf "${YELLOW}🎯 Modo aislado activo: Se probarán únicamente [ $SERVICES ]${NC}\n"
    else
        printf "${RED}❌ Error: Ninguno de los argumentos ingresados coincide con una ruta válida.${NC}\n"
        exit 1
    fi
else
    printf "${GREEN}🧪 Ejecutando los tests de todo el ecosistema de microservicios...${NC}\n"
fi
# ------------------------------------------------------------------

TOTAL_SERVICIOS=0
for S in $SERVICES; do
    TOTAL_SERVICIOS=$((TOTAL_SERVICIOS + 1))
done

CONTADOR_ACTUAL=0
OK_SERVICES=""
FAIL_SERVICES=""

for SERVICE in $SERVICES; do
    CONTADOR_ACTUAL=$((CONTADOR_ACTUAL + 1))

    printf "${GREEN}----------------------------------------------------${NC}\n"
    printf "${GREEN}Probando [${CONTADOR_ACTUAL}/${TOTAL_SERVICIOS}]: $SERVICE...${NC}\n"
    printf "${GREEN}----------------------------------------------------${NC}\n"

    if [ -d "$SERVICE" ]; then
        cd "$SERVICE" || exit
        if mvn test -Dtest='!*ApplicationTests' -DfailIfNoTests=false; then
            printf "${GREEN}✅ Tests OK en $SERVICE [${CONTADOR_ACTUAL}/${TOTAL_SERVICIOS}]${NC}\n"
            OK_SERVICES="$OK_SERVICES $SERVICE"
        else
            printf "${RED}❌ Tests fallaron en $SERVICE${NC}\n"
            FAIL_SERVICES="$FAIL_SERVICES $SERVICE"
        fi
        cd - > /dev/null
    else
        printf "${YELLOW}⚠️  Advertencia: El directorio $SERVICE no existe. Saltando...${NC}\n"
    fi
done

printf "${GREEN}======================================================${NC}\n"
printf "${GREEN}RESUMEN DE TESTS${NC}\n"
printf "${GREEN}======================================================${NC}\n"
for S in $OK_SERVICES; do
    printf "${GREEN}✅ %s${NC}\n" "$S"
done
for S in $FAIL_SERVICES; do
    printf "${RED}❌ %s${NC}\n" "$S"
done
printf "${GREEN}======================================================${NC}\n"

if [ -n "$FAIL_SERVICES" ]; then
    exit 1
fi
exit 0
