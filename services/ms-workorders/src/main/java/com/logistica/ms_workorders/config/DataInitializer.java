package com.logistica.ms_workorders.config;

import com.logistica.ms_workorders.model.Categoria;
import com.logistica.ms_workorders.model.OrdenTrabajo;
import com.logistica.ms_workorders.repository.OrdenTrabajoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final OrdenTrabajoRepository repository;

    public DataInitializer(OrdenTrabajoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repository.count() == 0) {
            OrdenTrabajo orden1 = new OrdenTrabajo();
            orden1.setBuildingId(1L);
            orden1.setDescripcion("Fuga de agua en departamento 302");
            orden1.setCategoria(Categoria.PLOMERIA);

            OrdenTrabajo orden2 = new OrdenTrabajo();
            orden2.setBuildingId(2L);
            orden2.setDescripcion("Corte de luz en piso 5");
            orden2.setCategoria(Categoria.ELECTRICIDAD);

            repository.saveAll(List.of(orden1, orden2));
            System.out.println(">> DataInitializer: Órdenes de trabajo base inicializadas.");
        } else {
            System.out.println(">> DataInitializer: Ya existen órdenes. Se omite la inicialización.");
        }
    }
}