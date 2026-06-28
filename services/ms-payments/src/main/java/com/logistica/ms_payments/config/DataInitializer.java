package com.logistica.ms_payments.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Los pagos dependen de facturas PENDIENTES que no existen al iniciar.
        // No se realiza seed inicial.
        System.out.println(">> DataInitializer ms-payments: sin datos semilla.");
    }
}