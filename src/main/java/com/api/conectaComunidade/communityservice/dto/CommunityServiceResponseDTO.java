package com.api.conectaComunidade.communityservice.dto;

import com.api.conectaComunidade.communityservice.entity.ServiceArea;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommunityServiceResponseDTO(
        Long id,
        String nome,
        ServiceArea area,
        LocalDate date,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        String local,
        Integer vagas,
        String description,
        Long contributorId,
        String contributorName
) {}