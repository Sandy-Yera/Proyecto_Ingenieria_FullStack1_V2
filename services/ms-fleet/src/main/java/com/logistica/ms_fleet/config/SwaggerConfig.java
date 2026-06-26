package com.logistica.ms_fleet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "ms-fleet — Gestión de Flotas",
        description = "Microservicio de control de vehículos y telemetría GPS en tiempo real del sistema BRM",
        version = "1.0.0",
        contact = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")
    ),
    servers = {
        @Server(url = "http://localhost:8086", description = "Local"),
        @Server(url = "http://api-gateway:8080/ms-fleet", description = "Via Gateway")
    }
)
@Configuration
public class SwaggerConfig { }
