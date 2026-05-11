#!/bin/bash

# Cada vez que lo quieran usar en una pc nueva, darle permisos:
# chmod +x compile-all.sh

# Colores para que la consola se vea pro
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}Iniciando compilación masiva de microservicios...${NC}"

# Lista de rutas de tus microservicios (iremos agregando más aquí)
SERVICES=(
    "infraestructure/eureka-server"
    # "infraestructure/api-gateway"  <--- Descomentar cuando lo tengamos listo
    # "domain/service-auth" <-- Descomenta cuando lo tengamos listo
)

for SERVICE in "${SERVICES[@]}"
do
    echo -e "${GREEN}Compilando: $SERVICE...${NC}"
    cd $SERVICE
    ./mvnw clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        echo "Error compitiendo $SERVICE. Abortando."
        exit 1
    fi
    
    cd - > /dev/null
done

echo -e "${GREEN}¡Todos los servicios han sido compilados con éxito!${NC}"
