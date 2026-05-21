package com.logistica.user.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 🔵 SOLUCIÓN MEDIO 1: Mudanza Estratégica del Interceptor Feign.
 * Este componente intercepta cada llamada de red saliente realizada por OpenFeign 
 * (desde ms-users hacia ms-auth) para inyectar de forma automática el X-Trace-Id
 * dinámico provisto por el API Gateway, unificando la traza distribuida.
 */
@Component
public class FeignInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        // Capturamos los atributos de la request HTTP que está procesando el hilo actual en ms-users
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String traceId = request.getHeader("X-Trace-Id");
            
            if (traceId != null) {
                // Sincronización de red: Adjunta el mismo ID a la petición remota saliente de OpenFeign
                template.header("X-Trace-Id", traceId);
            }
        }
    }
}