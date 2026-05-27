package example.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 🟠 SOLUCIÓN ALTO 3: Filtro Global Reactivo para Trazabilidad Distribuida.
 * Genera de forma dinámica un UUID único en tiempo de ejecución por CADA petición HTTP
 * que cruza el API Gateway, resolviendo el problema del ID estático del YAML.
 */
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Generar un identificador único real para ESTA petición HTTP específica
        String uniqueTraceId = UUID.randomUUID().toString();

        // 2. Modificar la petición de forma reactiva mutando los headers entrantes
        ServerHttpRequest requestConHeader = exchange.getRequest()
                .mutate()
                .header(TRACE_ID_HEADER, uniqueTraceId)
                .build();

        // 3. Reinyectar la petición modificada en el contexto del intercambio WebFlux
        ServerWebExchange exchangeMutado = exchange.mutate()
                .request(requestConHeader)
                .build();

        // Log local del Gateway para verificar que el enrutamiento inicia su viaje de manera trazable
        log.debug("Pipeline Reactivo - Asignando {} único [{}] para la ruta: {}", 
                TRACE_ID_HEADER, uniqueTraceId, exchange.getRequest().getURI().getPath());

        // 4. Continuar con la cadena de filtros hacia el microservicio correspondiente
        return chain.filter(exchangeMutado);
    }

    /**
     * Define la prioridad del filtro en el ciclo de vida del Gateway.
     * Al retornar Ordered.HIGHEST_PRECEDENCE (el valor numérico más bajo), aseguramos
     * que este filtro se ejecute ANTES que cualquier otro, garantizando que el Trace ID
     * esté disponible inmediatamente para la seguridad, auditoría o logs de ms-logs.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}