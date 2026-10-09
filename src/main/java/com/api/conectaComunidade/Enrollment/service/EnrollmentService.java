package com.api.conectaComunidade.Enrollment.service;

import com.api.conectaComunidade.Enrollment.dto.EnrollmentCancelResponseDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentCompleteResponseDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentRequestDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentResponseDTO;
import com.api.conectaComunidade.Enrollment.entity.Enrollment;
import com.api.conectaComunidade.Enrollment.entity.EnrollmentStatus;
import com.api.conectaComunidade.Enrollment.repository.EnrollmentRepository;
import com.api.conectaComunidade.communityservice.entity.CommunityService;
import com.api.conectaComunidade.communityservice.repository.CommunityServiceRepository;
import com.api.conectaComunidade.exception.BusinessException;
import com.api.conectaComunidade.exception.ResourceNotFoundException;
import com.api.conectaComunidade.user.entity.Role;
import com.api.conectaComunidade.user.entity.User;
import com.api.conectaComunidade.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        CommunityService service = communityServiceRepository
                .findById(request.serviceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Serviço não encontrado."
                        )
                );

        if (beneficiary.getRole() != Role.BENEFICIARY) {
            throw new BusinessException(
                    "Apenas beneficiários podem se inscrever em serviços."
            );
        }

        validateServiceDateTime(service, request.horario());

        validateHorario(service, request.horario());

        boolean horarioOcupado =
                enrollmentRepository.existsByServiceIdAndHorarioAndStatus(
                        service.getId(),
                        request.horario(),
                        EnrollmentStatus.ACTIVE
                );

        if (horarioOcupado) {
            throw new BusinessException(
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
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        Enrollment enrollment = enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inscrição não encontrada."
                        )
                );

        if (!enrollment.getBeneficiary().getId()
                .equals(beneficiary.getId())) {
            throw new BusinessException(
                    "Você não pode cancelar esta inscrição."
            );
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BusinessException(
                    "A inscrição não está ativa."
            );
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        enrollmentRepository.save(enrollment);

        return new EnrollmentCancelResponseDTO(
                "Inscrição cancelada com sucesso."
        );
    }

    public List<EnrollmentResponseDTO> findMyEnrollments(
            String email
    ) {
        User beneficiary = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        return enrollmentRepository
                .findByBeneficiaryId(beneficiary.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<EnrollmentResponseDTO> findByService(
            Long serviceId,
            String email
    ) {
        User contributor = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        CommunityService service = communityServiceRepository
                .findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Serviço não encontrado."
                        )
                );

        if (!service.getContributor().getId()
                .equals(contributor.getId())) {
            throw new BusinessException(
                    "Você não pode consultar as inscrições deste serviço."
            );
        }

        return enrollmentRepository
                .findByServiceId(serviceId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EnrollmentCompleteResponseDTO complete(
            Long enrollmentId,
            String email
    ) {
        User contributor = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        Enrollment enrollment = enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inscrição não encontrada."
                        )
                );

        if (!enrollment.getService().getContributor().getId()
                .equals(contributor.getId())) {
            throw new BusinessException(
                    "Você não pode concluir esta inscrição."
            );
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BusinessException(
                    "A inscrição não está ativa."
            );
        }

        enrollment.setStatus(EnrollmentStatus.COMPLETED);

        enrollmentRepository.save(enrollment);

        return new EnrollmentCompleteResponseDTO(
                "Inscrição concluída com sucesso."
        );
    }

    private void validateServiceDateTime(
            CommunityService service,
            LocalTime horario
    ) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (service.getDate().isBefore(today)) {
            throw new BusinessException(
                    "Não é possível se inscrever em um serviço que já aconteceu."
            );
        }

        if (service.getDate().isEqual(today)
                && !horario.isAfter(now)) {
            throw new BusinessException(
                    "Não é possível se inscrever em um horário que já passou."
            );
        }
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

        throw new BusinessException(
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
                enrollment.getStatus(),
                enrollment.getBeneficiary().getId(),
                enrollment.getBeneficiary().getName()
        );
    }
}