package com.ijse.bookstore.repository;

import com.ijse.bookstore.entity.OrderDetailReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailReadRepository extends JpaRepository<OrderDetailReadModel, Long> {
}
