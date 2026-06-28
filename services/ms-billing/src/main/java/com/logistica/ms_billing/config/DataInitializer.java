package com.logistica.ms_billing.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Las facturas dependen de órdenes COMPLETED que no existen al iniciar.
        // No se realiza seed inicial.
        System.out.println(">> DataInitializer ms-billing: sin datos semilla.");
    }
}