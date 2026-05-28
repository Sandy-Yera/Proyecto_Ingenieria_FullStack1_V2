package com.logistica.ms_quotes.config; // 🟢 Asegura que use guion bajo (_) igual que tus clientes

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String traceId = request.getHeader("X-Trace-Id");
            
            if (traceId != null && !traceId.isEmpty()) {
                template.header("X-Trace-Id", traceId);
            }
        }
    }
}
