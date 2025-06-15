package com.ijse.bookstore.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ijse.bookstore.entity.OrderDetails;
import com.ijse.bookstore.entity.Orders;
import com.ijse.bookstore.repository.OrderDetailsRepository;
import com.ijse.bookstore.repository.OrdersRepository;
import java.util.List;

@Service
public class OrderDetailsServiceImpl implements OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public OrderDetails createOrderDetails(OrderDetails orderDetails) {
        if (orderDetails.getOrderId() != null) {
            Orders order = ordersRepository.findById(orderDetails.getOrderId()).orElse(null);
            orderDetails.setOrder(order);
        }
        return orderDetailsRepository.save(orderDetails);
    }

    public List<OrderDetails> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailsRepository.findByOrder_Id(orderId);
    }
}
