package com.ijse.bookstore.repository;

import com.ijse.bookstore.entity.OrderReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderReadRepository extends JpaRepository<OrderReadModel, Long> {
    List<OrderReadModel> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
