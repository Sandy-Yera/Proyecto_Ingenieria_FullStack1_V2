package com.logistica.ms_payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsPaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPaymentsApplication.class, args);
    }
}