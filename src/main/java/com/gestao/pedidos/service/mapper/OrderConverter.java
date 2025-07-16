package com.gestao.pedidos.service.mapper;

import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.dto.response.OrderResponse;
import com.gestao.pedidos.model.Order;

import java.time.LocalDate;

public class OrderConverter {

    public static Order requestToOrder(OrderRequest request) {

        return Order.builder()
                .clientName(request.getClientName())
                .clientEmail(request.getClientEmail())
                .creationDate(LocalDate.now())
                .value(request.getValue())
                .build();
    }

    public static OrderResponse orderToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .clientEmail(order.getClientEmail())
                .build();
    }
}
