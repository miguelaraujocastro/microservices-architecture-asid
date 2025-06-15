package com.ijse.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ijse.bookstore.entity.ShippingOrder;
import com.ijse.bookstore.repository.ShippingOrderRepository;

@Service
public class ShippingOrderServiceImpl implements ShippingOrderService{
    
    @Autowired
    private ShippingOrderRepository shippingOrderRepository;


    @Override
    public ShippingOrder createShippingOrder(ShippingOrder shippingOrder){
        return shippingOrderRepository.save(shippingOrder);

    }

    public List<ShippingOrder> getAllShippingOrders(){
        return shippingOrderRepository.findAll();
    }

    @Override
    public ShippingOrder getById(Long id) {
        return shippingOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("ShippingOrder não encontrado com id: " + id));
    }

    @Override
    public ShippingOrder save(ShippingOrder shippingOrder) {
        return shippingOrderRepository.save(shippingOrder);
    }

    @Override
    public ShippingOrder findByOrderId(Long orderId) {
        return shippingOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("ShippingOrder não encontrado para orderId: " + orderId));
    }
}
