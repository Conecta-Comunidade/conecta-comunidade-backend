package com.api.conectaComunidade.communityservice.dto;

import com.api.conectaComunidade.communityservice.entity.ServiceArea;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CommunityServiceResponseDTO(
        Long id,
        String nome,
        ServiceArea area,
        LocalDate date,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        String local,
        Integer vagas,
        List<LocalTime> horariosDisponiveis,
        String description,
        Long contributorId,
        String contributorName
) {}