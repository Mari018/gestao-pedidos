package com.gestao.pedidos.repository;

import com.gestao.pedidos.enums.OrderState;
import com.gestao.pedidos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByState(OrderState state);

    List<Order> findAllByDate(LocalDate date);
}
