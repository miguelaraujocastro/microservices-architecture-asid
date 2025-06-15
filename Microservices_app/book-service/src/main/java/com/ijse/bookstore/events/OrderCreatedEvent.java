package com.ijse.bookstore.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements DomainEvent {

    private Long orderId;
    private Long userId;
    private List<CartItemDTO> cartItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long bookId;
        private int quantity;
    }
}
