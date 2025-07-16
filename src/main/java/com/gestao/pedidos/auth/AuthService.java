package com.gestao.pedidos.auth;

import com.gestao.pedidos.config.JwtService;
import com.gestao.pedidos.dto.request.LoginRequest;
import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.dto.response.AuthResponse;
import com.gestao.pedidos.exeption.InvalidTokenException;
import com.gestao.pedidos.exeption.UserNotFoundException;
import com.gestao.pedidos.model.User;
import com.gestao.pedidos.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User FindOrCreateUser(OrderRequest request) {

        if (!userRepository.existsByEmail(request.getClientEmail())) {
            LOGGER.info("Creating new user with email: {}", request.getClientEmail());
            User user = User.builder()
                    .email(request.getClientEmail())
                    .name(request.getClientName())
                    .isValidated(false)
                    .build();
            return userRepository.save(user);
        }

        LOGGER.info("User with email {} already exists", request.getClientEmail());
        return userRepository.findByEmail(request.getClientEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setToken(token);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .token(user.getToken())
                .refreshToken(user.getRefreshToken())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token format");
        }
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new InvalidTokenException("invalid refresh token");
        }

        String newToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        user.setToken(newToken);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        user.setToken(null);
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
