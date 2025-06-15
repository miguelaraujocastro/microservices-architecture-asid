package com.ijse.bookstore.kafka;

import com.ijse.bookstore.entity.Book;
import com.ijse.bookstore.events.OrderCreatedEvent;
import com.ijse.bookstore.events.StockUpdatedEvent;
import com.ijse.bookstore.repository.BookRepository;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.*;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Configuration
public class OrderCreatedListener {

  private final BookRepository bookRepo;
  private final DomainEventPublisher publisher;

  public OrderCreatedListener(BookRepository bookRepo,
                              DomainEventPublisher publisher) {
    this.bookRepo = bookRepo;
    this.publisher = publisher;
  }

  @Bean
  public DomainEventDispatcher orderCreatedDispatcher(DomainEventDispatcherFactory f) {
    return f.make("orderCreatedDispatcher",
           DomainEventHandlersBuilder
             .forAggregateType("com.ijse.bookstore.entity.ShippingOrder")
             .onEvent(OrderCreatedEvent.class, this::handle)
             .build());
  }

  @Transactional
  public void handle(DomainEventEnvelope<OrderCreatedEvent> env) {
    OrderCreatedEvent e = env.getEvent();
    e.getCartItems().forEach(item -> {
      Book b = bookRepo.findById(item.getBookId())
        .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + item.getBookId()));
      int updated = b.getQuantity() - item.getQuantity();
      if (updated < 0) throw new RuntimeException("Sem stock: " + item.getBookId());
      b.setQuantity(updated);
      bookRepo.save(b);
    });

    StockUpdatedEvent su = new StockUpdatedEvent(e.getOrderId(), e.getUserId());
    publisher.publish(
      "com.ijse.bookstore.entity.Book",
      e.getOrderId().toString(),
      Collections.<DomainEvent>singletonList(su));
  }
}
