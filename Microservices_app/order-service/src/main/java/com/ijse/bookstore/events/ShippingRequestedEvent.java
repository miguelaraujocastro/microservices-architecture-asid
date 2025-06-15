// ShippingRequestedEvent.java
package com.ijse.bookstore.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShippingRequestedEvent implements DomainEvent {
  private Long orderId;
  private Long userId;
  private Double total;
  private OrderRequestedEvent.ShippingInfo shippingInfo;
  private List<CartItemDTO> cartItems;

  @Getter @Setter
  @NoArgsConstructor @AllArgsConstructor
  public static class CartItemDTO {
    private Long bookId;
    private int quantity;
    private double subTotal;
  }
}
