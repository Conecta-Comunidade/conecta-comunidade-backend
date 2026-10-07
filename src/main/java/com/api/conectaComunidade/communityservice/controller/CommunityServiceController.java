package com.api.conectaComunidade.communityservice.controller;


import com.api.conectaComunidade.communityservice.dto.CommunityServiceRequestDTO;
import com.api.conectaComunidade.communityservice.dto.CommunityServiceResponseDTO;
import com.api.conectaComunidade.communityservice.service.CommunityServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community-services")
public class CommunityServiceController {

    private final CommunityServiceService communityServiceService;

    public CommunityServiceController(
            CommunityServiceService communityServiceService
    ) {
        this.communityServiceService = communityServiceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONTRIBUTOR')")
    public ResponseEntity<CommunityServiceResponseDTO> create(
            @Valid @RequestBody CommunityServiceRequestDTO request,
            Authentication authentication
    ) {
        CommunityServiceResponseDTO response =
                communityServiceService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
