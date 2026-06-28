package com.logistica.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "ms-users — Gestión de Usuarios",
        description = "Microservicio responsable del registro y gestión de usuarios del sistema BRM",
        version = "1.0.0",
        contact = @Contact(name = "Equipo BRM", email = "soporte@logistica.com")
    ),
    servers = {
        @Server(url = "http://localhost:8083", description = "Servidor local"),
        @Server(url = "${app.gateway-url:http://api-gateway:8080}/ms-users", description = "A través del API Gateway")
    }
)
@Configuration
public class SwaggerConfig {
    // Spring detecta @OpenAPIDefinition automáticamente.
    // No se requiere método @Bean adicional con springdoc-openapi 2.x
}
