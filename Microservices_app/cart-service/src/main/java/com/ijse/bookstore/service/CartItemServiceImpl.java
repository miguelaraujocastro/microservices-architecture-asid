package com.ijse.bookstore.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ijse.bookstore.entity.Cart;
import com.ijse.bookstore.entity.CartItem;
import com.ijse.bookstore.repository.CartItemRepository;
import com.ijse.bookstore.repository.CartRepository;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        if (cartItem.getCartId() != null) {
            Cart cart = cartRepository.findById(cartItem.getCartId()).orElse(null);
            cartItem.setCart(cart);
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getAllCartitem() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id).orElse(null);
    }

    @Override
    public CartItem patchCartQuantity(Long id, CartItem cartItem) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            existItem.setQuantity(cartItem.getQuantity());
            return cartItemRepository.save(existItem);
        }
        return null;
    }

    @Override
    public CartItem patchCartSubTotal(Long id, CartItem cartItem) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            existItem.setSubTotal(cartItem.getSubTotal());
            return cartItemRepository.save(existItem);
        }
        return null;
    }

    @Override
    public CartItem deleteCartItyItemById(Long id) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            cartItemRepository.delete(existItem);
        }
        return null;
    }

    @Override
    public void clearCart() {
        cartItemRepository.deleteAll(); 
    }

    @Override
    public void resetAutoIncrement() {
        cartItemRepository.resetAutoIncrement();
    }
}
