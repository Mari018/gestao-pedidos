package com.gestao.pedidos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "O nome do cliente não pode ser vazio")
    private String clientName;

    @NotBlank(message = "O email do cliente não pode ser vazio")
    @Email(message = "O email deve ter um formato válido")
    private String clientEmail;

    @Positive(message = "O valor deve ser positivo")
    private float value;
}


