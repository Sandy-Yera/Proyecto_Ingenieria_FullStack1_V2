package com.logistica.ms_payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "observaciones")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        this.fechaPago = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPago.PROCESANDO;
        }
    }
}