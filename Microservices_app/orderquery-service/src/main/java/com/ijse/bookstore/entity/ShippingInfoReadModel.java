package com.ijse.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shipping")
public class ShippingInfoReadModel {

    @Id
    @Column(name = "shippingorder_id")
    private Long shippingOrderId; // Corrigir nome para padronizar

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    private String postalCode;
    private String status;

    @OneToOne
    @JoinColumn(name = "order_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private OrderReadModel order;

    // Getters e setters

    public Long getShippingOrderId() {
        return shippingOrderId;
    }

    public void setShippingOrderId(Long shippingOrderId) {
        this.shippingOrderId = shippingOrderId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderReadModel getOrder() {
        return order;
    }

    public void setOrder(OrderReadModel order) {
        this.order = order;
    }

    // Adicione este getter para expor o orderId no JSON
    public Long getOrderId() {
        return order != null ? order.getOrderId() : null;
    }
}