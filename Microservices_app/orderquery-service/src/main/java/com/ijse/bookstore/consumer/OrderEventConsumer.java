// src/main/java/com/ijse/bookstore/consumer/OrderEventConsumer.java
package com.ijse.bookstore.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijse.bookstore.entity.OrderDetailReadModel;
import com.ijse.bookstore.entity.OrderReadModel;
import com.ijse.bookstore.entity.ShippingInfoReadModel;
import com.ijse.bookstore.repository.OrderReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderEventConsumer {

    @Autowired
    private OrderReadRepository orderRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Este listener consome o tópico "order-created", que foi publicado pelo OrderService.
     * Ele pega o JSON, constroi a entidade OrderReadModel (com OrderDetailsReadModel e ShippingInfoReadModel),
     * e salva tudo no banco `orderquerydb`.
     */
    @KafkaListener(topics = "order-created", groupId = "order-query")
    public void consume(String message) {
        try {
            JsonNode json = mapper.readTree(message);

            // ---------- Monta OrderReadModel ----------
            OrderReadModel order = new OrderReadModel();
            order.setOrderId(json.get("orderId").asLong());
            order.setUserId(json.get("userId").asLong());
            order.setOrderDate(LocalDateTime.now()); // ou usar um campo de timestamp se passado no evento
            BigDecimal total = new BigDecimal(json.get("total").asText());
            order.setTotalPrice(total);
            order.setStatus(json.get("status").asText());

            // Não tente setar shippingOrderId nem criar ShippingInfoReadModel aqui!
            // O shipping será criado/atualizado no evento shipping-completed

            // ---------- Monta lista de OrderDetailReadModel ----------
            List<OrderDetailReadModel> details = new ArrayList<>();
            for (JsonNode item : json.get("cartItems")) {
                OrderDetailReadModel detail = new OrderDetailReadModel();
                detail.setBookId(item.get("bookId").asLong());
                detail.setQuantity(item.get("quantity").asInt());
                detail.setSubTotal(item.get("subTotal").asDouble());
                detail.setOrder(order);
                details.add(detail);
            }
            order.setOrderDetails(details);

            // Persiste apenas o OrderReadModel (shipping será associado depois)
            orderRepository.save(order);
            System.out.println("Encomenda salva no orderquery-service: ID " + order.getOrderId());

        } catch (Exception e) {
            System.err.println("Erro ao processar evento Kafka 'order-created': " + e.getMessage());
            e.printStackTrace();
        }
    }
}
