package com.ijse.bookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ijse.bookstore.entity.Orders;
import com.ijse.bookstore.repository.OrdersRepository;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public Orders createOrder(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id).orElse(null);
    }

    @Override
    public void updateOrderStatus(Long id, String status) {
        Orders order = ordersRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        order.setStatus(status);
        ordersRepository.save(order);
    }
}