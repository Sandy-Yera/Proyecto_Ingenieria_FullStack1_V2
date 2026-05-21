#!/bin/bash

# Colores pro para la consola
GREEN='\033[0;32m'
RED='\033[0;31m'   # 🌟 Declarada la variable que faltaba
YELLOW='\033[0;33m'
NC='\033[0m'       # No Color

echo -e "${YELLOW}🔍 Buscando carpetas 'target' en el proyecto...${NC}"

# Contamos cuántas carpetas target existen antes de borrar
TOTAL_TARGETS=$(find . -type d -name "target" | wc -l)

if [ "$TOTAL_TARGETS" -eq 0 ]; then
    echo -e "${GREEN}✨ ¡El proyecto ya está limpio! No se encontraron carpetas 'target'.${NC}"
    exit 0
fi

echo -e "${YELLOW}🗑️  Se encontraron $TOTAL_TARGETS carpetas 'target'. Procesando eliminación...${NC}"

# 🟢 ADAPTACIÓN MULTIPLATAFORMA:
# Detectamos si el entorno actual es Windows (MSYS, MINGW o Cygwin a través de Git Bash)
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    echo -e "${YELLOW}💻 Entorno Windows detectado. Ejecutando rm directo sin sudo...${NC}"
    find . -type d -name "target" -exec rm -rf {} +
    CLEAN_STATUS=$?
else
    echo -e "${YELLOW}🐧 Entorno Linux/Unix detectado. Solicitando permisos de administrador (sudo)...${NC}"
    sudo find . -type d -name "target" -exec rm -rf {} +
    CLEAN_STATUS=$?
fi

# Evaluamos el resultado del comando de eliminación según el entorno
if [ $CLEAN_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ ¡Limpieza completada con éxito! Las carpetas target han sido destruidas. El proyecto quedó ligero y listo para la IA. 🚀${NC}"
else
    echo -e "${RED}❌ Hubo un problema al eliminar algunos archivos. Verifica los permisos de tu entorno.${NC}"
    exit 1
fi