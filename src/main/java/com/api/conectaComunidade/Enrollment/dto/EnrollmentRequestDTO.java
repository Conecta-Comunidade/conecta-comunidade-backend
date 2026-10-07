package com.api.conectaComunidade.Enrollment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record EnrollmentRequestDTO(

        @NotNull(message = "O serviço é obrigatório.")
        Long serviceId,

        @NotNull(message = "O horário é obrigatório.")
        LocalTime horario
) {
}
