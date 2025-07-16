package com.gestao.pedidos.repository;

import com.gestao.pedidos.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    List<ErrorLog> findByOccurredAtBetween(LocalDateTime yesterday, LocalDateTime today);
}
