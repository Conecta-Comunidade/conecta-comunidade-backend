package com.api.conectaComunidade.Enrollment.repository;

import com.api.conectaComunidade.Enrollment.entity.Enrollment;
import com.api.conectaComunidade.Enrollment.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByServiceIdAndHorarioAndStatus(
            Long serviceId,
            LocalTime horario,
            EnrollmentStatus status
    );

    List<Enrollment> findByBeneficiaryId(Long beneficiaryId);

    List<Enrollment> findByServiceId(Long serviceId);
}
