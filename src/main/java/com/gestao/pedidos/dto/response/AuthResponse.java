package com.gestao.pedidos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private long id;
    private String email;
    private String name;
    private String token;
    private String refreshToken;
}
