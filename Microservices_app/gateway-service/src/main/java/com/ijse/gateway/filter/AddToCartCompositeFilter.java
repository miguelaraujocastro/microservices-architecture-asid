package com.ijse.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AddToCartCompositeFilter implements GlobalFilter, Ordered {

    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        if (!exchange.getRequest().getPath().toString().contains("/gateway/add-to-cart")) {
            return chain.filter(exchange);
        }

        String bookId = exchange.getRequest().getQueryParams().getFirst("bookId");
        int quantity = Integer.parseInt(exchange.getRequest().getQueryParams().getFirst("quantity"));
        String username = exchange.getRequest().getHeaders().getFirst("X-User");

        if (username == null) {
            return sendJson(exchange, 401, "{ \"status\": \"fail\", \"message\": \"Unauthorized\" }");
        }

        return webClient.get()
            .uri("http://user-service:8085/id/" + username)
            .retrieve()
            .bodyToMono(UserResponse.class)
            .flatMap(user -> webClient.get()
                .uri("http://cart-service:8082/cart/" + user.getId())
                .retrieve()
                .bodyToMono(CartResponse.class)
                .flatMap(cart -> webClient.get()
                    .uri("http://book-service:8081/books/" + bookId)
                    .retrieve()
                    .bodyToMono(BookResponse.class)
                    .flatMap(book -> webClient.get()
                        .uri("http://cart-service:8082/cartitem")
                        .retrieve()
                        .bodyToFlux(CartItemResponse.class)
                        .collectList()
                        .flatMap(existingItems -> {
                            CartItemResponse existing = existingItems.stream()
                                .filter(item -> item.getBookId().equals(Long.parseLong(bookId)) &&
                                        item.getCartId() != null &&
                                        item.getCartId().equals(cart.getId()))
                                .findFirst()
                                .orElse(null);

                            int totalRequested = quantity;
                            if (existing != null) {
                                totalRequested += existing.getQuantity();
                            }

                            if (totalRequested > book.getQuantity()) {
                                return sendJson(exchange, 400, "{ \"status\": \"fail\", \"message\": \"Quantity exceeds stock\" }");
                            }

                            if (existing != null) {
                                int updatedQty = existing.getQuantity() + quantity;
                                double newSubtotal = updatedQty * book.getPrice();

                                return webClient.patch()
                                    .uri("http://cart-service:8082/quantity/" + existing.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(new CartItemUpdateRequest(updatedQty))
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .then(webClient.patch()
                                        .uri("http://cart-service:8082/subtotal/" + existing.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new CartItemSubtotalUpdateRequest(newSubtotal))
                                        .retrieve()
                                        .bodyToMono(Void.class))
                                    .then(sendJson(exchange, 200, "{ \"status\": \"updated\" }"));
                            }

                            CartItemRequest request = new CartItemRequest(
                                Long.parseLong(bookId),
                                user.getId(),
                                quantity,
                                book.getPrice(),
                                book.getPrice() * quantity,
                                cart.getId());

                            return webClient.post()
                                .uri("http://cart-service:8082/cartitem")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(resp -> sendJson(exchange, 200, "{ \"status\": \"created\" }"));
                        })
                    )
                )
    )
    .onErrorResume(error ->
        sendJson(exchange, 404, "{ \"status\": \"fail\", \"message\": \"User or cart not found\" }")
    );
    }

    private Mono<Void> sendJson(ServerWebExchange exchange, int statusCode, String jsonBody) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.valueOf(statusCode));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonBody.getBytes())));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private static class UserResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    private static class CartResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    private static class BookResponse {
        private int quantity;
        private double price;
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    private static class CartItemRequest {
        private Long bookId;
        private Long userId;
        private int quantity;
        private Double unitPrice;
        private Double subTotal;
        private Long cartId;

        public CartItemRequest(Long bookId, Long userId, int quantity, Double unitPrice, Double subTotal, Long cartId) {
            this.bookId = bookId;
            this.userId = userId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subTotal = subTotal;
            this.cartId = cartId;
        }

        public Long getBookId() { return bookId; }
        public Long getUserId() { return userId; }
        public int getQuantity() { return quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public Double getSubTotal() { return subTotal; }
        public Long getCartId() { return cartId; }
    }

    private static class CartItemResponse {
        private Long id;
        private Long bookId;
        private Long cartId;
        private int quantity;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Long getCartId() { return cartId; }
        public void setCartId(Long cartId) { this.cartId = cartId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    private static class CartItemUpdateRequest {
        private int quantity;

        public CartItemUpdateRequest(int quantity) {
            this.quantity = quantity;
        }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    private static class CartItemSubtotalUpdateRequest {
        private double subTotal;

        public CartItemSubtotalUpdateRequest(double subTotal) {
            this.subTotal = subTotal;
        }

        public double getSubTotal() { return subTotal; }
        public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    }
}