package com.ijse.bookstore.kafka;

import com.ijse.bookstore.entity.ShippingOrder;
import com.ijse.bookstore.events.ShippingCompletedEvent;
import com.ijse.bookstore.events.StockUpdatedEvent;
import com.ijse.bookstore.repository.ShippingOrderRepository;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Configuration
public class StockUpdatedToShippingCompletedListener {

  private final ShippingOrderRepository shippingOrderRepository;
  private final DomainEventPublisher domainEventPublisher;

  public StockUpdatedToShippingCompletedListener(ShippingOrderRepository shippingOrderRepository,
                                                 DomainEventPublisher domainEventPublisher) {
    this.shippingOrderRepository = shippingOrderRepository;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Bean
  public DomainEventDispatcher stockUpdatedToShippingCompletedDispatcher(DomainEventDispatcherFactory factory) {
    return factory.make(
      "stockUpdatedToShippingCompletedDispatcher",
      DomainEventHandlersBuilder
        .forAggregateType("com.ijse.bookstore.entity.Book")
        .onEvent(StockUpdatedEvent.class, this::handleStockUpdated)
        .build()
    );
  }

  @Transactional
  public void handleStockUpdated(DomainEventEnvelope<StockUpdatedEvent> env) {
    StockUpdatedEvent e = env.getEvent();
    Long orderId = e.getOrderId();

    // 1) Atualiza status do ShippingOrder para COMPLETED
    ShippingOrder so = shippingOrderRepository
      .findByOrderId(orderId)
      .orElseThrow(() -> new RuntimeException("ShippingOrder não encontrado para orderId: " + orderId));
    so.setStatus("COMPLETED");
    shippingOrderRepository.save(so);

    // 2) Publica ShippingCompletedEvent via Tram/outbox
    ShippingCompletedEvent evt = new ShippingCompletedEvent(orderId);
    domainEventPublisher.publish(
      "com.ijse.bookstore.entity.ShippingOrder",
      so.getId().toString(),
      Collections.<DomainEvent>singletonList(evt)
    );
  }
}
