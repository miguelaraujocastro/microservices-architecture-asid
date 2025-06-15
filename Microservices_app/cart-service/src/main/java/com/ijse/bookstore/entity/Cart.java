package com.ijse.bookstore.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartid")
    private Long id;

    private LocalDate createdDate;
    private Long userId;

    @OneToMany(mappedBy = "cart",
             cascade = CascadeType.ALL,
             orphanRemoval = true,   // <— adicionado!
             fetch = FetchType.EAGER)
    @JsonManagedReference  // <- ESSENCIAL para serialização
    private List<CartItem> cartItems;

    @Column(name = "status")
    private String status = "IDLE";

    // NOVO campo para indicar se o carrinho está bloqueado
    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    // Construtor padrão (JPA)
    public Cart() {
    }

    // Construtor auxiliar (caso crie Cart manualmente)
    public Cart(Long userId) {
        this.userId = userId;
        this.createdDate = LocalDate.now();
        this.status = "IDLE";
        this.locked = false;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}