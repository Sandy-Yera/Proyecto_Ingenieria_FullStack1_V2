package com.logistica.ms_auth.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore; // 🟢 Importación para ignorar en JSON

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_credenciales")
public class UserCredencial {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "El password es obligatorio")
    @JsonIgnore          // 🟢 Evita que el hash viaje en respuestas HTTP/JSON hacia el frontend u otros servicios
    @ToString.Exclude   // 🟢 Evita que se imprima accidentalmente en los logs del servidor
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}