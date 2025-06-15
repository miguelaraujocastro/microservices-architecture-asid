// OrderCreatedEvent.java
package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderCreatedEvent implements DomainEvent {
  private Long orderId;
  private Long userId;
  private Double total;
  private ShippingRequestedEvent.ShippingInfo shippingInfo;
  private List<ShippingRequestedEvent.CartItemDTO> cartItems;
  private Long shippingOrderId;
}
