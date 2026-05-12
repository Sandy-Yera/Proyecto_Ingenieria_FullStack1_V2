#!/bin/bash

# Cada vez que lo quieran usar en una pc nueva, darle permisos:
# chmod +x compile-all.sh

# Colores para que la consola se vea pro
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Iniciando compilación masiva de microservicios (Limpieza y Empaquetado)...${NC}"

# Lista de rutas de tus microservicios (Se agregan en orden de importancia)
SERVICES=(
    "infraestructure/eureka-server"
    "infraestructure/api-gateway"
    # --- Microservicios de Dominio ---
    # "services/service-auth"
    # "services/service-security"
    # "services/ms-users"
    # "services/ms-logistics"
    # "services/ms-workorders"
    # "services/service-logs"
)

for SERVICE in "${SERVICES[@]}"
do
    echo -e "${GREEN}----------------------------------------------------${NC}"
    echo -e "${GREEN}Compilando: $SERVICE...${NC}"
    echo -e "${GREEN}----------------------------------------------------${NC}"
    
    # Verificamos si el directorio existe antes de entrar
    if [ -d "$SERVICE" ]; then
        cd "$SERVICE" || exit
        
        # ./mvnw clean: Borra la carpeta /target (descompila lo viejo)
        # package: Genera el nuevo .jar
        # -DskipTests: Salta los tests para que la compilación sea veloz
        if ./mvnw clean package -DskipTests; then
            echo -e "${GREEN}✅ Exito en $SERVICE${NC}"
        else
            echo -e "${RED}❌ Error compilando $SERVICE. Abortando.${NC}"
            exit 1
        fi
        
        cd - > /dev/null
    else
        echo -e "${RED}⚠️  Advertencia: El directorio $SERVICE no existe. Saltando...${NC}"
    fi
done

echo -e "${GREEN}----------------------------------------------------${NC}"
echo -e "${GREEN}¡Todos los servicios han sido compilados con éxito!${NC}"
echo -e "${GREEN}Ya puedes ejecutar: docker-compose up --build -d${NC}"
echo -e "${GREEN}----------------------------------------------------${NC}"