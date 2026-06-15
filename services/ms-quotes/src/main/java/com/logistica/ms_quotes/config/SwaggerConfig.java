package com.logistica.ms_quotes.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURACIÓN SWAGGER / OpenAPI — ms-quotes
 * Define los metadatos del servicio que aparecen en la UI de Swagger (/swagger-ui.html).
 * Incluye título, descripción, versión y servidor base.
 *
 * CORRECCIÓN: Clase creada desde cero. El pom.xml tenía springdoc-openapi declarado
 * pero no había ninguna configuración ni documentación en los endpoints.
 */
@OpenAPIDefinition(
    info = @Info(
        title       = "ms-quotes — Gestión de Cotizaciones",
        description = "Microservicio responsable del ciclo de vida completo de cotizaciones " +
                      "de reparación dentro del sistema BRM (Building Repair Management). " +
                      "Permite crear, consultar, actualizar y eliminar cotizaciones, " +
                      "con filtros por usuario y estado.",
        version     = "1.0.0",
        contact     = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")
    ),
    servers = {
        @Server(url = "http://localhost:8086", description = "Servidor local"),
        @Server(url = "http://api-gateway:8080/ms-quotes", description = "A través del API Gateway")
    }
)
@Configuration
public class SwaggerConfig {
    // Spring detecta @OpenAPIDefinition automáticamente.
    // No se requiere método @Bean adicional con springdoc-openapi 2.x
}
