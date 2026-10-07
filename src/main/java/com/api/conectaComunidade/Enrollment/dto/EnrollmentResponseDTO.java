package com.api.conectaComunidade.Enrollment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EnrollmentResponseDTO(
        Long id,
        Long serviceId,
        String serviceName,
        LocalDate serviceDate,
        LocalTime horario,
        Long beneficiaryId,
        String beneficiaryName
) {}