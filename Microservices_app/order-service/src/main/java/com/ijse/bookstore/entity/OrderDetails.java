package com.ijse.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detailsid")
    private Long id;

    @Column
    private int quantity;

    @Column
    private double subTotal;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Orders order;

    @Transient
    private Long orderId;

    public OrderDetails() {
    }

    public OrderDetails(Long bookId, int quantity, double subTotal, Long userId, Orders order) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.userId = userId;
        this.order = order;
    }
}
