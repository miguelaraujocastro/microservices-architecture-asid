// StockUpdatedEvent.java
package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StockUpdatedEvent implements DomainEvent {
  private Long orderId;
  private Long userId;
}
