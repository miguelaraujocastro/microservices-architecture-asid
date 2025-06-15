package com.ijse.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ijse.bookstore.entity.OrderDetails;
import com.ijse.bookstore.service.OrderDetailsService;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OrderDetailsController {
    
    @Autowired
    private OrderDetailsService orderDetailsService;

    @PostMapping("/orderdetails")
    public ResponseEntity<OrderDetails> createOrderDetails(@RequestBody OrderDetails orderDetails){

        OrderDetails orderedDetails = orderDetailsService.createOrderDetails(orderDetails);

        return new ResponseEntity<>(orderedDetails,HttpStatus.CREATED);

    }

    @GetMapping("/orderdetails/order/{orderId}")
    public ResponseEntity<List<OrderDetails>> getOrderDetailsByOrderId(@PathVariable Long orderId){
        List<OrderDetails> orderDetails = orderDetailsService.getOrderDetailsByOrderId(orderId);
        if (orderDetails != null && !orderDetails.isEmpty()) {
            return new ResponseEntity<>(orderDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
