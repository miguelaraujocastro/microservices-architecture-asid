// OrderRequestedEvent.java
package com.ijse.bookstore.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderRequestedEvent implements DomainEvent {
  private String username;
  private ShippingInfo shippingInfo;

  @Getter @Setter
  @NoArgsConstructor @AllArgsConstructor
  public static class ShippingInfo {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    @JsonProperty("postal_code")
    private String postalCode;
  }
}