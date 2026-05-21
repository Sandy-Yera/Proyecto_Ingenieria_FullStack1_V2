package com.logistica.ms_logs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "service_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String level;

    //Se corrige poniendo 4000 para evitar truncados silenciosos
    @Column(length = 4000)
    private String message;

    // 🟢 SOLUCIÓN BAJO 3: Cambio de String a tipo temporal nativo Instant.
    // Esto mapeará automáticamente la columna en MySQL como un DATETIME(6).
    private Instant timestamp;
}