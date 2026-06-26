package com.logistica.ms_price_engine.config;

import com.logistica.ms_price_engine.model.Categoria;
import com.logistica.ms_price_engine.model.Tarifa; // Ajusta el import de tu entidad
import com.logistica.ms_price_engine.repository.TarifaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final TarifaRepository tarifaRepository;

    // Inyección por constructor (la opción más limpia y recomendada)
    public DataInitializer(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. Verificar si la tabla está vacía
        if (tarifaRepository.count() == 0) {

            // 2. Crear las instancias de las tarifas base
            // (Asumiendo que tu entidad Tarifa tiene un constructor con estos parámetros)
            Tarifa plomeria = new Tarifa(null, Categoria.PLOMERIA, 25000.0, 8000.0);
            Tarifa electricidad = new Tarifa(null, Categoria.ELECTRICIDAD, 30000.0, 12000.0);
            Tarifa gas = new Tarifa(null, Categoria.GAS, 35000.0, 15000.0);

            // 3. Guardar todo en una sola operación por eficiencia
            tarifaRepository.saveAll(List.of(plomeria, electricidad, gas));

            System.out.println(">> DataInitializer: Tarifas base inicializadas con éxito.");
        } else {
            System.out
                    .println(">> DataInitializer: Ya existen tarifas en la base de datos. Se omite la inicialización.");
        }
    }
}