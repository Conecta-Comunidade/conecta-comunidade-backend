package com.api.conectaComunidade.Enrollment.service;

import com.api.conectaComunidade.Enrollment.dto.EnrollmentCancelResponseDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentRequestDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentResponseDTO;
import com.api.conectaComunidade.Enrollment.entity.Enrollment;
import com.api.conectaComunidade.Enrollment.entity.EnrollmentStatus;
import com.api.conectaComunidade.Enrollment.repository.EnrollmentRepository;
import com.api.conectaComunidade.communityservice.entity.CommunityService;
import com.api.conectaComunidade.communityservice.repository.CommunityServiceRepository;
import com.api.conectaComunidade.user.entity.Role;
import com.api.conectaComunidade.user.entity.User;
import com.api.conectaComunidade.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class EnrollmentService {

    private static final int SLOT_DURATION_MINUTES = 30;

    private final EnrollmentRepository enrollmentRepository;
    private final CommunityServiceRepository communityServiceRepository;
    private final UserRepository userRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CommunityServiceRepository communityServiceRepository,
            UserRepository userRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.communityServiceRepository = communityServiceRepository;
        this.userRepository = userRepository;
    }

    public EnrollmentResponseDTO create(
            EnrollmentRequestDTO request,
            String email
    ) {
        User beneficiary = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        CommunityService service = communityServiceRepository
                .findById(request.serviceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado."));

        if (beneficiary.getRole() != Role.BENEFICIARY) {
            throw new IllegalArgumentException(
                    "Apenas beneficiários podem se inscrever em serviços."
            );
        }

        validateHorario(service, request.horario());

        boolean horarioOcupado =
                enrollmentRepository.existsByServiceIdAndHorarioAndStatus(
                        service.getId(),
                        request.horario(),
                        EnrollmentStatus.ACTIVE
                );

        if (horarioOcupado) {
            throw new IllegalArgumentException(
                    "O horário selecionado já está ocupado."
            );
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setHorario(request.horario());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setBeneficiary(beneficiary);
        enrollment.setService(service);

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return toResponseDTO(savedEnrollment);
    }

    public EnrollmentCancelResponseDTO cancel(
            Long enrollmentId,
            String email
    ) {
        User beneficiary = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        Enrollment enrollment = enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new RuntimeException("Inscrição não encontrada.")
                );

        if (!enrollment.getBeneficiary().getId().equals(beneficiary.getId())) {
            throw new IllegalArgumentException(
                    "Você não pode cancelar esta inscrição."
            );
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "A inscrição não está ativa."
            );
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        enrollmentRepository.save(enrollment);

        return new EnrollmentCancelResponseDTO(
                "Inscrição cancelada com sucesso."
        );
    }

    public List<EnrollmentResponseDTO> findMyEnrollments(String email) {
        User beneficiary = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        return enrollmentRepository
                .findByBeneficiaryId(beneficiary.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private void validateHorario(
            CommunityService service,
            LocalTime horario
    ) {
        LocalTime horarioAtual = service.getHorarioInicio();

        for (int i = 0; i < service.getVagas(); i++) {

            if (horarioAtual.equals(horario)) {
                return;
            }

            horarioAtual = horarioAtual.plusMinutes(
                    SLOT_DURATION_MINUTES
            );
        }

        throw new IllegalArgumentException(
                "O horário selecionado não corresponde a uma vaga disponível."
        );
    }

    private EnrollmentResponseDTO toResponseDTO(
            Enrollment enrollment
    ) {
        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getService().getId(),
                enrollment.getService().getName(),
                enrollment.getService().getDate(),
                enrollment.getHorario(),
                enrollment.getBeneficiary().getId(),
                enrollment.getBeneficiary().getName()
        );
    }
}