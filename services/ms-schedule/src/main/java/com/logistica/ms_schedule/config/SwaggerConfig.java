package com.logistica.ms_schedule.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "ms-schedule — Gestión de Bloques Horarios",
        description = "Microservicio de programación de bloques horarios de técnicos con validación anti-solapamiento del sistema BRM",
        version = "1.0.0",
        contact = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")
    ),
    servers = {
        @Server(url = "http://localhost:8098", description = "Local"),
        @Server(url = "${app.gateway-url:http://api-gateway:8080}/ms-schedule", description = "Via Gateway")
    }
)
@Configuration
public class SwaggerConfig { }
