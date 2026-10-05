package com.api.conectaComunidade.auth.dto;

import com.api.conectaComunidade.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDTO(
        @NotBlank(message = "Nome é obrigatório.")
        String name,

        @NotBlank(message = "Email é obrigatório.")
        @Email
        String email,

        @NotBlank(message = "Senha é obrigatório.")
        @Size(min = 3 , max = 8, message = "Senha de ver entre 3 e 8 caracteris.")
        String password,

        @NotNull
        Role role

) {
}
