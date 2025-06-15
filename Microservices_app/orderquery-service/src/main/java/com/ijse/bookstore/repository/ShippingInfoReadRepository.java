package com.ijse.bookstore.repository;

import com.ijse.bookstore.entity.ShippingInfoReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingInfoReadRepository extends JpaRepository<ShippingInfoReadModel, Long> {
}
