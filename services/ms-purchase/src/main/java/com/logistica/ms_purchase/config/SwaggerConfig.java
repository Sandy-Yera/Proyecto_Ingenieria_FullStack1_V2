package com.logistica.ms_purchase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "ms-purchase — Gestión de Compras",
        description = "Microservicio de registro de compras a proveedores y actualización de inventario del sistema BRM",
        version = "1.0.0",
        contact = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")
    ),
    servers = {
        @Server(url = "http://localhost:8096", description = "Local"),
        @Server(url = "${app.gateway-url:http://api-gateway:8080}/ms-purchase", description = "Via Gateway")
    }
)
@Configuration
public class SwaggerConfig { }
