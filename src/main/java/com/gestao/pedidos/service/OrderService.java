package com.gestao.pedidos.service;

import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.dto.response.OrderResponse;
import com.gestao.pedidos.enums.OrderState;
import com.gestao.pedidos.exeption.OrderNotFoundException;
import com.gestao.pedidos.model.Order;
import com.gestao.pedidos.repository.OrderRepository;
import com.gestao.pedidos.service.mapper.OrderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ExternalValidationService externalValidationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
        LOGGER.info("Creating order for client: {}", orderRequest.getClientEmail());
        Order order = OrderConverter.requestToOrder(orderRequest);

        return OrderConverter.orderToResponse(order);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("Order with id {} not found", id);
            return new OrderNotFoundException("Order not found");
        });

        return OrderConverter.orderToResponse(order);
    }

    public List<OrderResponse> listForState(OrderState state) {
        List<Order> orders = orderRepository.findAllByState(state);

        return orders.stream()
                .map(OrderConverter::orderToResponse)
                .toList();
    }

    public List<OrderResponse> listForDate(LocalDate date) {
        List<Order> orders = orderRepository.findAllByDate(date);

        return orders.stream()
                .map(OrderConverter::orderToResponse)
                .toList();
    }
}
