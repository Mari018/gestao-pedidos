package com.gestao.pedidos.auth;

import com.gestao.pedidos.dto.request.LoginRequest;
import com.gestao.pedidos.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<AuthResponse> refreshToken(String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> logout(String token) {
        authService.logout(token);
       return ResponseEntity.ok().build();
    }
}
