package com.ijse.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shippingorder_id")
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String address;

    @Column 
    private String city;

    @Column 
    private String email;

    @Column 
    private String postal_code;

    @Column(name = "order_id")
    private Long orderId;

    // Construtor personalizado sem id (melhor prática)
    public ShippingOrder(String firstName, String lastName, String address, String city, String email, String postal_code) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.email = email;
        this.postal_code = postal_code;
        this.status = "PENDING";
        this.orderId = null; // ainda não foi criada a Order
    }

    @Column(name = "status")
    private String status = "PENDING";
}
