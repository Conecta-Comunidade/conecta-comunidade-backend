package com.api.conectaComunidade.auth.dto;

import com.api.conectaComunidade.user.entity.Role;

public record RegisterResponseDTO(
        Long id,
        String nome,
        String email,
        Role role

) {
}
