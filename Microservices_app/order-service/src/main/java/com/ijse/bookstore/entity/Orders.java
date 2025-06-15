package com.ijse.bookstore.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long id;

    @Column
    private Date orderDate;

    @Column
    private double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderDetails> orderDetails;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status")
    private String status = "PENDING";

    public Orders() {
    }

    public Orders(Date orderDate, double totalPrice, Long shippingOrderId, Long userId) {
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }
}
