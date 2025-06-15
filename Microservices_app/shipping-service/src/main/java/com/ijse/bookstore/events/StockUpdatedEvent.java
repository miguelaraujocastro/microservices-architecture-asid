package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Disparado pelo book-service quando o estoque foi atualizado.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StockUpdatedEvent implements DomainEvent {
  private Long orderId;
  private Long userId;
}