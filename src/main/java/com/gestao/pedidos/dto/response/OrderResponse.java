package com.gestao.pedidos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private long id;
    private String clientEmail;
}
