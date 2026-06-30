#!/bin/bash

# Nombre: run-tests.sh
# Objetivo: Ejecutar los tests unitarios de uno, varios o todos los microservicios sin
# necesitar Maven instalado en el host. Usa el mismo contenedor Maven aislado que ya
# usan compile-only.sh / build-project.sh, así corre igual en cualquier PC con Docker.
#
# Uso:
#   ./run-tests.sh                  -> corre los tests de TODOS los microservicios
#   ./run-tests.sh ms-fleet ms-logs -> corre solo los tests de los servicios indicados

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

# Limpieza preventiva de formatos (CRLF -> LF), igual que el resto de los scripts del repo
sed -i 's/\r$//' run-tests.sh 2>/dev/null || true
sed -i 's/\r$//' test-all.sh 2>/dev/null || true

echo -e "${YELLOW}🔑 Asegurando permisos de ejecución...${NC}"
chmod +x test-all.sh

MAVEN_CACHE_DIR="$HOME/.m2"
mkdir -p "$MAVEN_CACHE_DIR"

if [ $# -gt 0 ]; then
    echo -e "${YELLOW}🎯 Modo selectivo activo. Probando servicios: $@${NC}"
else
    echo -e "${GREEN}🧪 Modo general activo. Se probará todo el ecosistema de microservicios.${NC}"
fi

# Ejecuta los tests dentro del mismo contenedor Maven/Alpine que usa el resto del proyecto
# para compilar, así no hace falta instalar Maven ni un JDK en el host.
MSYS_NO_PATHCONV=1 docker run --rm -it \
    -v "$(pwd)":/app \
    -v "$MAVEN_CACHE_DIR":/root/.m2 \
    -w /app \
    maven:3.9.6-eclipse-temurin-21-alpine \
    sh test-all.sh "$@"

TEST_STATUS=$?

if [ $TEST_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ Todos los tests ejecutados pasaron correctamente.${NC}"
else
    echo -e "${RED}❌ Hubo al menos un servicio con tests fallidos. Revisa el resumen de arriba.${NC}"
fi

exit $TEST_STATUS
