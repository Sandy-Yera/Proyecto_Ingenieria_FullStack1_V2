#!/bin/bash

# Nombre: build-project.sh
# Objetivo: Compilar todo el ecosistema sin configurar nada en la PC host.

# Colores para la consola
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

echo -e "${YELLOW}🧹 Limpiando formatos de archivo (Windows -> Linux)...${NC}"
sed -i 's/\r$//' build-project.sh 2>/dev/null || true
sed -i 's/\r$//' compile-all.sh 2>/dev/null || true

echo -e "${YELLOW}🔑 Asegurando permisos de ejecución...${NC}"
chmod +x compile-all.sh

echo -e "${GREEN}🚀 Iniciando proceso de construcción universal...${NC}"

# Carpeta de caché de Maven en el host para acelerar las imágenes simples
MAVEN_CACHE_DIR="$HOME/.m2"
mkdir -p "$MAVEN_CACHE_DIR"

echo -e "${YELLOW}📦 Usando contenedor aislado de Maven para empaquetar servicios JRE...${NC}"

# Ejecutamos el contenedor de compilación para los Jars locales obligatorios
MSYS_NO_PATHCONV=1 docker run --rm -it \
    -v "$(pwd)":/app \
    -v "$MAVEN_CACHE_DIR":/root/.m2 \
    -w /app \
    maven:3.9.6-eclipse-temurin-21-alpine \
    sh compile-all.sh

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Paso 1: Compilación base exitosa. Levantando entorno con Docker Compose...${NC}"
    
    # 🧼 AUTOMATIZACIÓN ALTERNATIVA KAFKA: Limpieza preventiva de volúmenes huérfanos/desfasados
    # Esto asegura que Kafka y Zookeeper sincronicen sus IDs desde cero sin intervención manual.
    echo -e "${YELLOW}🛡️  Sincronizando estado de mensajería: Removiendo volúmenes previos de Kafka/Zookeeper...${NC}"
    docker compose -f docker/infra-docker/compose.yml down -v 2>/dev/null || true

    # CORRECCIÓN 2026: Usamos 'docker compose' (V2 nativo) y apuntamos al compose unificado actualizado
    # Nota: Si tu compose está en la raíz, se deja así. Si sigue en la subcarpeta, restáuralo a 'docker compose -f docker/infra-docker/compose.yml up --build -d'
    docker compose -f docker/infra-docker/compose.yml up --build -d

    echo -e "${YELLOW}🧹 Limpiando imágenes intermedias residuales (dangling)...${NC}"
    docker image prune -f

    echo -e "${GREEN}🚀 ¡Todo el ecosistema BRM está arriba!${NC}"
    echo -e "${GREEN}🔗 Panel de control (Eureka): http://localhost:8761${NC}"
    echo -e "${GREEN}🔗 API Gateway (Único ingreso): http://localhost:9090${NC}"
else
    echo -e "${RED}❌ Error: La compilación en el contenedor de Maven falló. Revisa los logs de arriba.${NC}"
    exit 1
fi