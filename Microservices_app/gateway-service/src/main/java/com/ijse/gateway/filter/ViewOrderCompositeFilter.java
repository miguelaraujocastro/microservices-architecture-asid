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
import java.util.stream.Collectors;

@Component
public class ViewOrderCompositeFilter implements GlobalFilter, Ordered {

    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        // Verifica se o path da requisição contém "/gateway/view-order/"
        if (!exchange.getRequest().getPath().toString().contains("/gateway/view-order/")) {
            return chain.filter(exchange);
        }

        // Extrai o orderId do path da requisição
        String orderIdString = exchange.getRequest().getPath().pathWithinApplication().value().substring("/gateway/view-order/".length());
        Long orderId;
        try {
            orderId = Long.parseLong(orderIdString);
        } catch (NumberFormatException e) {
            return sendJson(exchange, 400, "{ \"status\": \"fail\", \"message\": \"Invalid Order ID\" }");
        }

        // Obtém o username do header para autorização
        String username = exchange.getRequest().getHeaders().getFirst("X-User");

        if (username == null) {
            return sendJson(exchange, 401, "{ \"status\": \"fail\", \"message\": \"Unauthorized\" }");
        }

        // 1. Chamar o user-service para obter o ID do usuário
        return webClient.get()
                .uri("http://user-service:8085/id/" + username)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .flatMap(user -> {
                    // 2. Chamar o order-service para obter os detalhes da encomenda
                    Mono<OrderResponse> orderMono = webClient.get()
                            .uri("http://order-service:8083/orders/" + orderId)
                            .retrieve()
                            .bodyToMono(OrderResponse.class);

                    // 3. Chamar o order-service para obter os itens da encomenda
                    Mono<List<OrderDetailResponse>> orderDetailsMono = webClient.get()
                            .uri("http://order-service:8083/orderdetails/order/" + orderId)
                            .retrieve()
                            .bodyToFlux(OrderDetailResponse.class)
                            .collectList();

                    // 4. Chamar o shipping-service para obter os detalhes de envio
                    Mono<ShippingResponse> shippingMono = webClient.get()
                            .uri("http://shipping-service:8084/shipping/order/" + orderId)
                            .retrieve()
                            .bodyToMono(ShippingResponse.class);

                    // Combinar os resultados de todas as chamadas
                    return Mono.zip(orderMono, orderDetailsMono, shippingMono)
                            .flatMap(tuple -> {
                                OrderResponse order = tuple.getT1();
                                List<OrderDetailResponse> orderDetails = tuple.getT2();
                                ShippingResponse shipping = tuple.getT3();

                                // Verifica se o usuário da encomenda corresponde ao usuário autenticado
                                if (!order.getUserId().equals(user.getId())) {
                                    return sendJson(exchange, 403, "{ \"status\": \"fail\", \"message\": \"Forbidden: Order does not belong to this user\" }");
                                }

                                // Constrói a resposta JSON consolidada
                                StringBuilder jsonBuilder = new StringBuilder();
                                jsonBuilder.append("{");
                                jsonBuilder.append("\"status\": \"success\",");
                                jsonBuilder.append("\"order\": {");
                                jsonBuilder.append("\"id\": ").append(order.getId()).append(",");
                                jsonBuilder.append("\"userId\": ").append(order.getUserId()).append(",");
                                jsonBuilder.append("\"orderDate\": \"").append(order.getOrderDate()).append("\",");
                                jsonBuilder.append("\"totalPrice\": ").append(order.getTotalPrice ()).append(",");
                                jsonBuilder.append("\"status\": \"").append(order.getStatus()).append("\",");
                                jsonBuilder.append("\"orderDetails\": [");
                                for (int i = 0; i < orderDetails.size(); i++) {
                                    OrderDetailResponse detail = orderDetails.get(i);
                                    jsonBuilder.append("{");
                                    jsonBuilder.append("\"id\": ").append(detail.getId()).append(",");
                                    jsonBuilder.append("\"bookId\": ").append(detail.getBookId()).append(",");
                                    jsonBuilder.append("\"quantity\": ").append(detail.getQuantity()).append(",");
                                    jsonBuilder.append("\"subTotal\": ").append(detail.getSubTotal());
                                    jsonBuilder.append("}");
                                    if (i < orderDetails.size() - 1) {
                                        jsonBuilder.append(",");
                                    }
                                }
                                jsonBuilder.append("]");
                                jsonBuilder.append("},"); // Fecha "order"
                                jsonBuilder.append("\"shipping\": {");
                                jsonBuilder.append("\"id\": ").append(shipping.getId()).append(",");
                                jsonBuilder.append("\"orderId\": ").append(shipping.getOrderId()).append(",");
                                jsonBuilder.append("\"address\": \"").append(shipping.getAddress()).append("\",");
                                jsonBuilder.append("\"city\": \"").append(shipping.getCity()).append("\",");
                                jsonBuilder.append("\"postal_code\": \"").append(shipping.getPostal_code()).append("\",");
                                jsonBuilder.append("\"status\": \"").append(shipping.getStatus()).append("\",");
                                jsonBuilder.append("}"); // Fecha "shipping"
                                jsonBuilder.append("}"); // Fecha o objeto principal

                                return sendJson(exchange, 200, jsonBuilder.toString());
                            })
                            .onErrorResume(e -> {
                                // Loga o erro para depuração
                                System.err.println("Error fetching order details: " + e.getMessage());
                                return sendJson(exchange, 500, "{ \"status\": \"error\", \"message\": \"Internal Server Error: Could not retrieve order details.\" }");
                            });
                })
                .onErrorResume(error ->
                        sendJson(exchange, 404, "{ \"status\": \"fail\", \"message\": \"User not found or order not found\" }")
                );
    }

    /**
     * Envia uma resposta JSON para o cliente com o status code e corpo especificados.
     * @param exchange O ServerWebExchange.
     * @param statusCode O código de status HTTP.
     * @param jsonBody O corpo da resposta JSON.
     * @return Um Mono<Void> que representa a conclusão da operação.
     */
    private Mono<Void> sendJson(ServerWebExchange exchange, int statusCode, String jsonBody) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.valueOf(statusCode));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonBody.getBytes())));
    }

    @Override
    public int getOrder() {
        // Define a ordem de execução do filtro. Valores menores têm maior precedência.
        return -2; // Definido com uma ordem diferente do AddToCartCompositeFilter para evitar conflitos se ambos forem ativados para o mesmo path (embora não seja o caso aqui).
    }

    // Classes DTO para as respostas dos microsserviços
    private static class UserResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    private static class OrderResponse {
        private Long id;
        private Long userId;
        private String orderDate; // Pode ser LocalDateTime ou String dependendo da serialização do Order Service
        private Double totalPrice;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        public Double getTotalPrice () { return totalPrice; }
        public void setTotalPrice (Double totalPrice) { this.totalPrice = totalPrice; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    private static class OrderDetailResponse {
        private Long id;
        private Long orderId;
        private Long bookId;
        private int quantity;
        private Double subTotal;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public Double getSubTotal() { return subTotal; }
        public void setSubTotal(Double subTotal) { this.subTotal = subTotal; }
    }

    private static class ShippingResponse {
        private Long id;
        private Long orderId;
        private String address;
        private String city;
        private String postal_code;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getPostal_code() { return postal_code; }
        public void setPostal_code(String postal_code) { this.postal_code = postal_code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}