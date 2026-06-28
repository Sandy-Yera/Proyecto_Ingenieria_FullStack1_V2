package com.logistica.ms_billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsBillingApplication.class, args);
    }
}