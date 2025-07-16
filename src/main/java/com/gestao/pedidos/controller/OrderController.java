package com.gestao.pedidos.controller;

import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.dto.response.OrderResponse;
import com.gestao.pedidos.enums.OrderState;
import com.gestao.pedidos.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(path = "/state")
    public ResponseEntity<List<OrderResponse>> listForState(@RequestParam OrderState state) {
        List<OrderResponse> orderResponses = orderService.listForState(state);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping(path = "/date")
    public ResponseEntity<List<OrderResponse>> listForDate(@RequestParam LocalDate date) {
        List<OrderResponse> orderResponses = orderService.listForDate(date);
        return ResponseEntity.ok(orderResponses);
    }

}
