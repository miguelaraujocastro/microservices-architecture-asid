package com.ijse.bookstore.service;

import com.ijse.bookstore.entity.Orders;

public interface OrdersService {
    Orders createOrder(Orders order);
    Orders getOrderById(Long id);
    void updateOrderStatus(Long id, String status);
}