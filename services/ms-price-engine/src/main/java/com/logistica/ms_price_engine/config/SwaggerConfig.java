package com.logistica.ms_price_engine.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURACIÓN SWAGGER / OpenAPI — ms-price-engine
 * Define los metadatos del servicio que aparecen en la UI de Swagger
 * (/swagger-ui.html).
 * Incluye título, descripción, versión y servidor base.
 *
 * CORRECCIÓN: Clase creada desde cero. El pom.xml tenía springdoc-openapi
 * declarado
 * pero no había ninguna configuración ni documentación en los endpoints.
 */
@OpenAPIDefinition(info = @Info(title = "ms-price-engine — Gestión de Precios", description = "Microservicio responsable del cálculo y gestión de precios de reparación dentro del sistema BRM (Building Repair Management). "
        +
        "Permite calcular precios basados en categorías, horas de trabajo y unidades de material.", version = "1.0.0", contact = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")), servers = {
                @Server(url = "http://localhost:8095", description = "Servidor local"),
                @Server(url = "http://api-gateway:8080/ms-price-engine", description = "A través del API Gateway")
        })
@Configuration
public class SwaggerConfig {
    // Spring detecta @OpenAPIDefinition automáticamente.
    // No se requiere método @Bean adicional con springdoc-openapi 2.x
}
