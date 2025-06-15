package com.ijse.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private Double unitPrice;
    private Double subTotal;
    private Long bookId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long cartIdHelper;

    @Transient
    @JsonProperty("cartId")
    public Long getCartId() {
        return cart != null ? cart.getId() : cartIdHelper;
    }

    public void setCartId(Long cartId) {
        this.cartIdHelper = cartId;
    }
}