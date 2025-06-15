package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShippingCompletedEvent implements DomainEvent {
  private Long orderId;
}
