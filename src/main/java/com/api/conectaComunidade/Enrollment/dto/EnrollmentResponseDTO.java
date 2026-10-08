package com.api.conectaComunidade.Enrollment.dto;

import com.api.conectaComunidade.Enrollment.entity.EnrollmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record EnrollmentResponseDTO(
        Long id,
        Long serviceId,
        String serviceName,
        LocalDate serviceDate,
        LocalTime horario,
        EnrollmentStatus status,
        Long beneficiaryId,
        String beneficiaryName
) {
}