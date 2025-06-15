package com.ijse.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.ijse.bookstore.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE cart_item AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findAllByCartId(Long cartId);

    /**
     * Para deletar em lote todos os CartItem cujo Cart está associado a cartId,
     * usamos exatamente "ci.cart.id" (e não "ci.cartId").
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(Long cartId);

}