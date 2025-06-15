package com.ijse.bookstore.controller;

import com.ijse.bookstore.entity.Cart;
import com.ijse.bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<Cart> createCart(@RequestBody Cart createCart) {
        Cart updatedCart = cartService.createCart(createCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @GetMapping("/cart")
    public ResponseEntity<List<Cart>> getAllCart() {
        List<Cart> existCart = cartService.getAllCart();
        return new ResponseEntity<>(existCart, HttpStatus.OK);
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<Cart> getCartIdByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartIdByUserId(userId);
        return cart != null
                ? new ResponseEntity<>(cart, HttpStatus.OK)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/lock/{id}")
    public ResponseEntity<Void> lockCart(@PathVariable Long id) {
        cartService.lockCart(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unlock/{id}")
    public ResponseEntity<Void> unlockCart(@PathVariable Long id) {
        cartService.unlockCart(id);
        return ResponseEntity.ok().build();
    }
}