#!/bin/bash

# Nombre: clean-targets.sh
# Objetivo: Eliminar de forma segura y multiplataforma todas las carpetas 'target'.

# Colores pro para la consola
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

echo -e "${YELLOW}🔍 Buscando carpetas 'target' en el espacio de trabajo...${NC}"

# Contamos cuántas carpetas target existen antes de borrar
TOTAL_TARGETS=$(find . -type d -name "target" | wc -l)

# Limpiamos espacios en blanco del conteo
TOTAL_TARGETS=$(echo "$TOTAL_TARGETS" | tr -d ' ')

if [ "$TOTAL_TARGETS" -eq 0 ] || [ -z "$TOTAL_TARGETS" ]; then
    echo -e "${GREEN}✨ ¡El proyecto ya está limpio! No se encontraron carpetas 'target'.${NC}"
    exit 0
fi

echo -e "${YELLOW}🗑️  Se encontraron $TOTAL_TARGETS carpetas 'target'. Procesando eliminación...${NC}"

# Intentamos borrar de forma directa (funciona en Windows y en Linux si los archivos son del usuario actual)
find . -type d -name "target" -exec rm -rf {} + 2>/dev/null
CLEAN_STATUS=$?

# 🐧 FALLBACK DE SEGURIDAD PARA LINUX:
# Si falló el primer intento debido a archivos creados por contenedores 'root' antiguos
if [ $CLEAN_STATUS -ne 0 ] && [[ "$OSTYPE" != "msys" && "$OSTYPE" != "cygwin" ]]; then
    echo -e "${YELLOW}🔒 Se detectaron archivos protegidos. Solicitando elevación (sudo) para forzar la limpieza...${NC}"
    sudo find . -type d -name "target" -exec rm -rf {} +
    CLEAN_STATUS=$?
fi

# Evaluamos el resultado final
if [ $CLEAN_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ ¡Limpieza completada! El espacio de trabajo ha quedado 100% ligero y listo. 🚀${NC}"
else
    echo -e "${RED}❌ Error: No se pudieron eliminar todas las carpetas. Revisa si algún proceso o IDE tiene los archivos bloqueados.${NC}"
    exit 1
fi