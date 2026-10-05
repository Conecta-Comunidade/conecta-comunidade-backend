package com.api.conectaComunidade.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email
        String email,

        @Size(min = 3 , max = 8, message = "Senha deve ter entre 3 e 8 carateris")
        String password
) {
}
