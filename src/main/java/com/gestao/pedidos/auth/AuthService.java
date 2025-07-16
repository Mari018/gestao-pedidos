package com.gestao.pedidos.auth;

import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.exeption.UserNotFoundException;
import com.gestao.pedidos.model.User;
import com.gestao.pedidos.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateUser(OrderRequest request) {

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
}
