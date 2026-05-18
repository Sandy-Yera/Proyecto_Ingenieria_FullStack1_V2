package com.logistica.ms_auth.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
/**
 * OPTIMIZACIÓN: Se cambia el nombre de la tabla a snake_case y minúsculas para 
 * garantizar compatibilidad absoluta con motores de bases de datos en contenedores Linux.
 */
@Table(name = "user_credenciales")
public class UserCredencial {

    @Id
    @NotNull(message = "El ID es obligatorio")
    @Column(name = "user_id") // Deja en claro que mapea al ID del perfil remoto
    private Long id; 

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "El password es obligatorio")
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive; 

    /**
     * OPTIMIZACIÓN: Se migra de java.sql.Timestamp a LocalDateTime para cumplir 
     * con los estándares modernos de auditoría temporal en Java 21.
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * CICLO DE VIDA DE PERSISTENCIA
     * Asegura que al insertar la fila por primera vez, el usuario nazca activo 
     * y sin registros de login previos falsos.
     */
    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    // CORRECCIÓN: Se elimina @PreUpdate automático para evitar que se pise el 'lastLogin' 
    // cuando el usuario simplemente actualice su cuenta o correo electrónico.
}