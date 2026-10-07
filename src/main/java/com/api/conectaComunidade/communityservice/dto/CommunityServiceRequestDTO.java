package com.api.conectaComunidade.communityservice.dto;

import com.api.conectaComunidade.communityservice.entity.ServiceArea;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommunityServiceRequestDTO(

        @NotBlank(message = "Nome é obrigatório.")
        String name,

        @NotNull(message = "Área é obrigatória.")
        ServiceArea area,

        @NotNull(message = "Data é obrigatória.")
        @FutureOrPresent(message = "A data deve ser igual ou posterior à data atual.")
        LocalDate date,

        @NotNull(message = "Horário de início é obrigatório.")
        LocalTime horarioInicio,

        @NotBlank(message = "Local é obrigatório.")
        String local,

        @NotNull(message = "Quantidade de vagas é obrigatória.")
        @Min(value = 1, message = "A quantidade de vagas deve ser maior que zero.")
        Integer vagas,

        @NotBlank(message = "Descrição é obrigatória.")
        String description
) {
}