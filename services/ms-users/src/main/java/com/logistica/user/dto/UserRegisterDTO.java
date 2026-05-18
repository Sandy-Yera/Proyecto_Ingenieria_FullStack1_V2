package com.logistica.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class UserRegisterDTO {
    
    // CORREGIDO: Ahora es String y recibe el RUT completo (ej: "12345678-K") sin separar el DV.
    @NotBlank(message = "El RUT completo es obligatorio")
    private String rut; 

    // ELIMINADO: El campo 'private String dv;' ya no existe aquí de raíz.

    @NotBlank(message = "El primer nombre es obligatorio")
    private String pNombre;

    private String sNombre;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String apPat;

    private String apMat;
    
    // MEJORA: Pasado a String para soportar formatos telefónicos internacionales modernos sin truncar datos.
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono; 

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo es inválido")
    private String correo;

    @NotBlank(message = "La contraseña de acceso es obligatoria")
    @ToString.Exclude
    private String password;
}