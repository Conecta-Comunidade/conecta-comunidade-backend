package com.api.conectaComunidade.user.dto;

import com.api.conectaComunidade.user.entity.Role;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Role role) {
}