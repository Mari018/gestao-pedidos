package com.gestao.pedidos.service;

import com.gestao.pedidos.auth.AuthService;
import com.gestao.pedidos.dto.request.OrderRequest;
import com.gestao.pedidos.dto.response.OrderResponse;
import com.gestao.pedidos.enums.OrderState;
import com.gestao.pedidos.exeption.OrderNotFoundException;
import com.gestao.pedidos.model.ErrorLog;
import com.gestao.pedidos.model.Order;
import com.gestao.pedidos.model.OrderStatusHistory;
import com.gestao.pedidos.model.User;
import com.gestao.pedidos.repository.ErrorLogRepository;
import com.gestao.pedidos.repository.OrderRepository;
import com.gestao.pedidos.repository.OrderStatusHistoryRepository;
import com.gestao.pedidos.service.mapper.OrderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ExternalValidationService externalValidationService;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final AuthService authService;
    private final ErrorLogRepository errorLogRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository, ExternalValidationService externalValidationService, OrderStatusHistoryRepository statusHistoryRepository, AuthService authService, ErrorLogRepository errorLogRepository) {
        this.orderRepository = orderRepository;
        this.externalValidationService = externalValidationService;
        this.statusHistoryRepository = statusHistoryRepository;
        this.authService = authService;
        this.errorLogRepository = errorLogRepository;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {

        try {
            User user = authService.findOrCreateUser(orderRequest);

            if (!user.isValidated()) {
                ExternalValidationService.ValidationResult validationService = externalValidationService.validateClient(user);

                user.setValidated(validationService.isValid());
            }

            Order order = OrderConverter.requestToOrder(orderRequest);
            orderRepository.save(order);

            OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                    .order(order)
                    .state(order.getState())
                    .build();

            statusHistoryRepository.save(statusHistory);

            return OrderConverter.orderToResponse(order);
        } catch (Exception e) {
            LOGGER.error("Error creating order for client {}: {}", orderRequest.getClientEmail(), e.getMessage());

            ErrorLog errorLog = ErrorLog.builder()
                    .errorType("EXTERNAL_VALIDATION_ERROR")
                    .errorMessage("Failed to validate client: " + e.getMessage())
                    .stackTrace(Arrays.toString(e.getStackTrace()))
                    .endpoint("/order")
                    .httpMethod("Post")
                    .userEmail(orderRequest.getClientEmail())
                    .occurredAt(LocalDateTime.now())
                    .build();

            errorLogRepository.save(errorLog);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
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
