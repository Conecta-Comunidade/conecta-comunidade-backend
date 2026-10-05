package com.api.conectaComunidade.auth.service;


import com.api.conectaComunidade.auth.dto.LoginRequestDTO;
import com.api.conectaComunidade.auth.dto.LoginResponseDTO;
import com.api.conectaComunidade.auth.dto.RegisterRequestDTO;
import com.api.conectaComunidade.auth.dto.RegisterResponseDTO;
import com.api.conectaComunidade.security.JwtService;
import com.api.conectaComunidade.user.entity.User;
import com.api.conectaComunidade.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponseDTO login (LoginRequestDTO request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("E-mail ou senha inválidos")
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("E-mail ou senha inválidos");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponseDTO(
                token
        );
    }
}