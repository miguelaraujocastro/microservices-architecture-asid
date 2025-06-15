package com.ijse.bookstore.repository;

import com.ijse.bookstore.entity.OrderReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReadModelRepository extends JpaRepository<OrderReadModel, Long> {
    OrderReadModel findByOrderId(Long orderId);
}
