package com.logistica.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <- Asegúrate de que se importe

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // <- ¡ESTA ES LA ANOTACIÓN QUE FALTA!
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}