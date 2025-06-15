package com.ijse.bookstore.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.ijse.bookstore.entity.ShippingOrder;



@Service
public interface ShippingOrderService {
    
    ShippingOrder createShippingOrder(ShippingOrder shippingOrder);

    List<ShippingOrder> getAllShippingOrders();

    ShippingOrder getById(Long id);

    ShippingOrder save(ShippingOrder shippingOrder);

    ShippingOrder findByOrderId(Long orderId);
}
