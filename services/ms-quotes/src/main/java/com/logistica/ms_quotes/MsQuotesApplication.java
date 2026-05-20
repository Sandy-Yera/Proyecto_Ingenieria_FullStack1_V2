package com.logistica.ms_quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <- Asegura este import

@SpringBootApplication
@EnableFeignClients // 🚀 ¡Esta anotación activa la magia de los clientes que acabamos de crear!
public class MsQuotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsQuotesApplication.class, args);
    }
}