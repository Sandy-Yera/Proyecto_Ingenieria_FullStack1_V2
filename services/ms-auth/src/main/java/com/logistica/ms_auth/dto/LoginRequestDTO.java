package com.logistica.ms_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequestDTO {

    @NotBlank
    @Email
    private String username;

    @NotBlank
    @ToString.Exclude
    private String password;
}