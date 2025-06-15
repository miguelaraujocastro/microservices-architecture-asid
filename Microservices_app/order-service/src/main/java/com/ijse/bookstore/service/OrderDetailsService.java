package com.ijse.bookstore.service;
import org.springframework.stereotype.Service;
import com.ijse.bookstore.entity.OrderDetails;
import java.util.List;

@Service
public interface OrderDetailsService {
    OrderDetails createOrderDetails(OrderDetails orderDetails);
    List<OrderDetails> getOrderDetailsByOrderId(Long orderId);
}
