package com.ijse.bookstore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ijse.bookstore.entity.OrderDetails;
import com.ijse.bookstore.entity.Orders;
import com.ijse.bookstore.events.ShippingRequestedEvent;
import com.ijse.bookstore.events.OrderRequestedEvent.ShippingInfo;
import com.ijse.bookstore.repository.OrderDetailsRepository;
import com.ijse.bookstore.repository.OrdersRepository;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    // --- endpoints existentes ---

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody Orders order) {
        Orders saved = ordersRepository.save(order);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        return ordersRepository.findById(id)
                .map(o -> ResponseEntity.ok(o))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
                                                  @PathVariable String status) {
        ordersRepository.findById(id).ifPresent(o -> {
            o.setStatus(status);
            ordersRepository.save(o);
        });
        return ResponseEntity.ok().build();
    }

    // --- novo endpoint de API Composition para checkout ---

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(@RequestHeader("X-User") String username,
                                        @RequestBody ShippingInfo shippingInfo) {

        WebClient client = webClientBuilder.build();

        // 1) obter userId
        JsonNode userJson = client.get()
                .uri("http://user-service:8085/id/{username}", username)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        Long userId = userJson.get("id").asLong();

        // 2) buscar e bloquear carrinho
        JsonNode cartJson = client.get()
                .uri("http://cart-service:8082/cart/{userId}", userId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        Long cartId = cartJson.get("id").asLong();

        client.put()
            .uri("http://cart-service:8082/lock/{cartId}", cartId)
            .retrieve()
            .bodyToMono(Void.class)
            .block();

        // 3) buscar itens no carrinho
        List<ShippingRequestedEvent.CartItemDTO> items = client.get()
                .uri("http://cart-service:8082/cartitem")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .filter(n -> cartId.equals(n.get("cartId").asLong()))
                .map(n -> new ShippingRequestedEvent.CartItemDTO(
                        n.get("bookId").asLong(),
                        n.get("quantity").asInt(),
                        n.get("subTotal").asDouble()))
                .collectList()
                .block();

        // 4) calcular total
        double total = items.stream()
                            .mapToDouble(ShippingRequestedEvent.CartItemDTO::getSubTotal)
                            .sum();

        // 5) criar Orders + OrderDetails
        Orders order = new Orders(new Date(), total, null, userId);
        order.setStatus("PENDING");
        ordersRepository.save(order);

        items.forEach(dto -> {
            OrderDetails detail = new OrderDetails(
                    dto.getBookId(),
                    dto.getQuantity(),
                    dto.getSubTotal(),
                    userId,
                    order);
            orderDetailsRepository.save(detail);
        });

        // 6) publicar evento via outbox
        ShippingRequestedEvent evt = new ShippingRequestedEvent(
                order.getId(),
                userId,
                total,
                shippingInfo,
                items);

        domainEventPublisher.publish(
                "com.ijse.bookstore.entity.Orders",
                order.getId().toString(),
                Collections.singletonList(evt));

        return ResponseEntity.accepted().build();
    }

}
