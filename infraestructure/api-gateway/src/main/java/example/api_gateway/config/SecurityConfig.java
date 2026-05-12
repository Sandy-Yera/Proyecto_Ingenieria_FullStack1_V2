package example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para que permita peticiones POST/PUT desde Postman
            .authorizeExchange(exchanges -> exchanges
                .anyExchange().permitAll() // POR AHORA: Permitimos todo para poder probar los microservicios
            )
            .build();
    }
}