package com.gestao.pedidos.exeption.errorhandling;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private String message;
    private String method;
    private String requestURI;
    private Date timeStamp;
}
