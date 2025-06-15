package com.ijse.bookstore.kafka;

import com.ijse.bookstore.entity.ShippingOrder;
import com.ijse.bookstore.events.ShippingRequestedEvent;
import com.ijse.bookstore.events.OrderCreatedEvent;
import com.ijse.bookstore.repository.ShippingOrderRepository;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.*;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Configuration
public class ShippingRequestedListener {

  private final ShippingOrderRepository shippingOrderRepo;
  private final DomainEventPublisher publisher;

  public ShippingRequestedListener(ShippingOrderRepository shippingOrderRepo,
                                   DomainEventPublisher publisher) {
    this.shippingOrderRepo = shippingOrderRepo;
    this.publisher = publisher;
  }

  @Bean
  public DomainEventDispatcher shippingRequestedDispatcher(DomainEventDispatcherFactory f) {
    return f.make("shippingRequestedDispatcher",
           DomainEventHandlersBuilder
             .forAggregateType("com.ijse.bookstore.entity.Orders")
             .onEvent(ShippingRequestedEvent.class, this::handle)
             .build());
  }

  @Transactional
  public void handle(DomainEventEnvelope<ShippingRequestedEvent> env) {
    ShippingRequestedEvent e = env.getEvent();
    ShippingOrder so = new ShippingOrder(
        e.getShippingInfo().getFirstName(),
        e.getShippingInfo().getLastName(),
        e.getShippingInfo().getAddress(),
        e.getShippingInfo().getCity(),
        e.getShippingInfo().getEmail(),
        e.getShippingInfo().getPostalCode());
    so.setOrderId(e.getOrderId());
    so.setStatus("PENDING");
    shippingOrderRepo.save(so);

    OrderCreatedEvent oc = new OrderCreatedEvent(
        e.getOrderId(),
        e.getUserId(),
        e.getTotal(),
        e.getShippingInfo(),
        e.getCartItems(),
        so.getId());

    publisher.publish(
      "com.ijse.bookstore.entity.ShippingOrder",
      so.getId().toString(),
      Collections.<DomainEvent>singletonList(oc));
  }
}
