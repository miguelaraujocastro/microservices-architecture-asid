package com.ijse.bookstore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ijse.bookstore.entity.Cart;
import com.ijse.bookstore.repository.CartRepository;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart createCart(Cart cart) {
    cart.setCreatedDate(LocalDate.now());
    return cartRepository.save(cart);
}

    @Override
    public List<Cart> getAllCart() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getCartIdByUserId(Long userId) {
        return cartRepository.getCartIdByUserId(userId);
    }

    @Override
    public void lockCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
        cart.setStatus("IN_PROGRESS");
        cartRepository.save(cart);
    }

    @Override
    public void unlockCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
        cart.setStatus("IDLE");
        cartRepository.save(cart);
    }
}