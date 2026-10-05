package com.api.conectaComunidade.auth.controller;


import com.api.conectaComunidade.auth.dto.RegisterRequestDTO;
import com.api.conectaComunidade.auth.dto.RegisterResponseDTO;
import com.api.conectaComunidade.auth.service.AuthService;
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, @Nullable HandlerMapping resourceHandlerMapping) {
        this.authService = authService;
    }

    @PostMapping("/register")
    private ResponseEntity<RegisterResponseDTO> register (@Valid @RequestBody RegisterRequestDTO register){
            RegisterResponseDTO response = authService.register(register);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
