package com.ijse.bookstore.kafka;

import com.ijse.bookstore.entity.Cart;
import com.ijse.bookstore.events.CartClearedEvent;
import com.ijse.bookstore.events.StockUpdatedEvent;
import com.ijse.bookstore.repository.CartItemRepository;
import com.ijse.bookstore.repository.CartRepository;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.*;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Configuration
public class StockUpdatedListener {

  private final CartRepository cartRepo;
  private final CartItemRepository cartItemRepo;
  private final DomainEventPublisher publisher;

  public StockUpdatedListener(CartRepository cartRepo,
                              CartItemRepository cartItemRepo,
                              DomainEventPublisher publisher) {
    this.cartRepo = cartRepo;
    this.cartItemRepo = cartItemRepo;
    this.publisher = publisher;
  }

  @Bean
  public DomainEventDispatcher stockUpdatedDispatcher(DomainEventDispatcherFactory f) {
    return f.make("stockUpdatedDispatcher",
           DomainEventHandlersBuilder
             .forAggregateType("com.ijse.bookstore.entity.Book")
             .onEvent(StockUpdatedEvent.class, this::handle)
             .build());
  }

  @Transactional
  public void handle(DomainEventEnvelope<StockUpdatedEvent> env) {
      StockUpdatedEvent e = env.getEvent();
      Long userId = e.getUserId();
      Long orderId = e.getOrderId();

      Cart cart = cartRepo.getCartIdByUserId(userId);
      if (cart == null) {
          throw new RuntimeException("Carrinho não encontrado para userId: " + userId);
      }
      Long cartId = cart.getId();

      // Apaga todos os itens daquele carrinho
      cartItemRepo.deleteByCartId(cartId);
      cartItemRepo.flush();

      // Limpa a lista em memória para garantir sincronização JPA
      cart.getCartItems().clear();

      // Desbloqueia e salva o carrinho
      cart.setLocked(false);
      cartRepo.save(cart);

      // Publica o CartClearedEvent
      CartClearedEvent cc = new CartClearedEvent(userId, orderId, "cart cleared");
      publisher.publish(
          "com.ijse.bookstore.entity.Cart",
          cartId.toString(),
          Collections.singletonList(cc)
      );
  }
}
