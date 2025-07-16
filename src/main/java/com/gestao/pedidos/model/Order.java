package com.gestao.pedidos.model;

import com.gestao.pedidos.enums.OrderState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "requests")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_state", nullable = false)
    private OrderState state = OrderState.PENDING;

    @Column(name = "value", nullable = false)
    private float value;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory;
}
