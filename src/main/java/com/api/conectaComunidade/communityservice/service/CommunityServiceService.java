package com.api.conectaComunidade.communityservice.service;

import com.api.conectaComunidade.communityservice.dto.CommunityServiceRequestDTO;
import com.api.conectaComunidade.communityservice.dto.CommunityServiceResponseDTO;
import com.api.conectaComunidade.communityservice.entity.CommunityService;
import com.api.conectaComunidade.communityservice.repository.CommunityServiceRepository;
import com.api.conectaComunidade.user.entity.User;
import com.api.conectaComunidade.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class CommunityServiceService {

    private static final int DURACAO_VAGA_MINUTOS = 30;

    private final CommunityServiceRepository communityServiceRepository;
    private final UserRepository userRepository;

    public CommunityServiceService(
            CommunityServiceRepository communityServiceRepository,
            UserRepository userRepository
    ) {
        this.communityServiceRepository = communityServiceRepository;
        this.userRepository = userRepository;
    }

    public CommunityServiceResponseDTO create(
            CommunityServiceRequestDTO request,
            String email
    ) {
        User contributor = userRepository
                .findByEmail(email)
                .orElseThrow();

        CommunityService communityService = new CommunityService();

        communityService.setName(request.name());
        communityService.setArea(request.area());
        communityService.setDate(request.date());
        communityService.setHorarioInicio(request.horarioInicio());
        communityService.setLocal(request.local());
        communityService.setVagas(request.vagas());
        communityService.setDescription(request.description());
        communityService.setContributor(contributor);

        CommunityService savedService =
                communityServiceRepository.save(communityService);

        return toResponseDTO(savedService);
    }

    private CommunityServiceResponseDTO toResponseDTO(
            CommunityService communityService
    ) {
        LocalTime horarioFim = calculateEndTime(
                communityService.getHorarioInicio(),
                communityService.getVagas()
        );

        return new CommunityServiceResponseDTO(
                communityService.getId(),
                communityService.getName(),
                communityService.getArea(),
                communityService.getDate(),
                communityService.getHorarioInicio(),
                horarioFim,
                communityService.getLocal(),
                communityService.getVagas(),
                communityService.getDescription(),
                communityService.getContributor().getId(),
                communityService.getContributor().getName()
        );
    }

    private LocalTime calculateEndTime(
            LocalTime horarioInicio,
            Integer vagas
    ) {
        return horarioInicio.plusMinutes(
                (long) vagas * DURACAO_VAGA_MINUTOS
        );
    }
}