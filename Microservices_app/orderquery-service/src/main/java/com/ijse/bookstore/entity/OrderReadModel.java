package com.ijse.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderReadModel {

    @Id
    private Long orderId;

    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private String status;

    @Column(name = "shippingorder_id")
    private Long shippingOrderId;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private ShippingInfoReadModel shipping;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetailReadModel> orderDetails;

    // Getters e setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ShippingInfoReadModel getShipping() {
        return shipping;
    }

    public void setShipping(ShippingInfoReadModel shipping) {
        this.shipping = shipping;
    }

    public List<OrderDetailReadModel> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailReadModel> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Long getShippingOrderId() {
        return shippingOrderId;
    }

    public void setShippingOrderId(Long shippingOrderId) {
        this.shippingOrderId = shippingOrderId;
    }
}
