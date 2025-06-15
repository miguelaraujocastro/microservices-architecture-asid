// CartClearedEvent.java
package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CartClearedEvent implements DomainEvent {
  private Long userId;
  private Long orderId;
  private String status;  // e.g. "cart cleared"
}