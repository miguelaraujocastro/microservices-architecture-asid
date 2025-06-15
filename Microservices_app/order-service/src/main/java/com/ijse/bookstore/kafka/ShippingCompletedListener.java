package com.ijse.bookstore.kafka;

import com.ijse.bookstore.entity.Orders;
import com.ijse.bookstore.events.ShippingCompletedEvent;
import com.ijse.bookstore.repository.OrdersRepository;
import io.eventuate.tram.events.subscriber.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ShippingCompletedListener {

  private final OrdersRepository ordersRepository;

  public ShippingCompletedListener(OrdersRepository ordersRepository) {
    this.ordersRepository = ordersRepository;
  }

  @Bean
  public DomainEventDispatcher shippingCompletedToOrderCompletedDispatcher(DomainEventDispatcherFactory factory) {
    return factory.make(
      "shippingCompletedToOrderCompletedDispatcher",
      DomainEventHandlersBuilder
        .forAggregateType("com.ijse.bookstore.entity.ShippingOrder")
        .onEvent(ShippingCompletedEvent.class, this::handleShippingCompleted)
        .build()
    );
  }

  @Transactional
  public void handleShippingCompleted(DomainEventEnvelope<ShippingCompletedEvent> env) {
    ShippingCompletedEvent e = env.getEvent();
    Long orderId = e.getOrderId();

    // Atualiza status de Orders para COMPLETED
    Orders order = ordersRepository.findById(orderId)
      .orElseThrow(() -> new RuntimeException("Ordem não encontrada: " + orderId));
    order.setStatus("COMPLETED");
    ordersRepository.save(order);
  }
}
