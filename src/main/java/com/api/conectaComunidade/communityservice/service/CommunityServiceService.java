package com.api.conectaComunidade.communityservice.service;

import com.api.conectaComunidade.Enrollment.entity.EnrollmentStatus;
import com.api.conectaComunidade.Enrollment.repository.EnrollmentRepository;
import com.api.conectaComunidade.communityservice.dto.CommunityServiceRequestDTO;
import com.api.conectaComunidade.communityservice.dto.CommunityServiceResponseDTO;
import com.api.conectaComunidade.communityservice.entity.CommunityService;
import com.api.conectaComunidade.communityservice.repository.CommunityServiceRepository;
import com.api.conectaComunidade.exception.ResourceNotFoundException;
import com.api.conectaComunidade.user.entity.User;
import com.api.conectaComunidade.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityServiceService {

    private static final int DURACAO_VAGA_MINUTOS = 30;

    private final CommunityServiceRepository communityServiceRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CommunityServiceService(
            CommunityServiceRepository communityServiceRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.communityServiceRepository = communityServiceRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public CommunityServiceResponseDTO create(
            CommunityServiceRequestDTO request,
            String email
    ) {
        User contributor = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

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

    public List<CommunityServiceResponseDTO> findAll() {
        return communityServiceRepository
                .findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private CommunityServiceResponseDTO toResponseDTO(
            CommunityService communityService
    ) {
        LocalTime horarioFim = calculateEndTime(
                communityService.getHorarioInicio(),
                communityService.getVagas()
        );

        List<LocalTime> horariosDisponiveis =
                calculateAvailableTimes(communityService);

        return new CommunityServiceResponseDTO(
                communityService.getId(),
                communityService.getName(),
                communityService.getArea(),
                communityService.getDate(),
                communityService.getHorarioInicio(),
                horarioFim,
                communityService.getLocal(),
                communityService.getVagas(),
                horariosDisponiveis,
                communityService.getDescription(),
                communityService.getContributor().getId(),
                communityService.getContributor().getName()
        );
    }

    private List<LocalTime> calculateAvailableTimes(
            CommunityService communityService
    ) {
        List<LocalTime> horariosDisponiveis = new ArrayList<>();

        LocalTime horarioAtual = communityService.getHorarioInicio();

        for (int i = 0; i < communityService.getVagas(); i++) {

            boolean horarioOcupado =
                    enrollmentRepository.existsByServiceIdAndHorarioAndStatus(
                            communityService.getId(),
                            horarioAtual,
                            EnrollmentStatus.ACTIVE
                    );

            if (!horarioOcupado) {
                horariosDisponiveis.add(horarioAtual);
            }

            horarioAtual = horarioAtual.plusMinutes(
                    DURACAO_VAGA_MINUTOS
            );
        }

        return horariosDisponiveis;
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