package com.api.conectaComunidade.Enrollment.controller;

import com.api.conectaComunidade.Enrollment.dto.EnrollmentCancelResponseDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentRequestDTO;
import com.api.conectaComunidade.Enrollment.dto.EnrollmentResponseDTO;
import com.api.conectaComunidade.Enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<EnrollmentResponseDTO> create(
            @Valid @RequestBody EnrollmentRequestDTO request,
            Authentication authentication
    ) {

        EnrollmentResponseDTO response =
                enrollmentService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<EnrollmentCancelResponseDTO> cancel(
            @PathVariable Long id,
            Authentication authentication
    ) {
        EnrollmentCancelResponseDTO response =
                enrollmentService.cancel(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<List<EnrollmentResponseDTO>> findMyEnrollments(
            Authentication authentication
    ) {
        List<EnrollmentResponseDTO> response =
                enrollmentService.findMyEnrollments(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}