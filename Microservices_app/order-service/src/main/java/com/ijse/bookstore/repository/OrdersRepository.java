package com.ijse.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ijse.bookstore.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
}