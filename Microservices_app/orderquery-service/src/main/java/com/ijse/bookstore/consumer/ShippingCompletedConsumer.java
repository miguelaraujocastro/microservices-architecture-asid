package com.ijse.bookstore.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijse.bookstore.entity.OrderReadModel;
import com.ijse.bookstore.entity.ShippingInfoReadModel;
import com.ijse.bookstore.repository.OrderReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingCompletedConsumer {

    @Autowired
    private OrderReadModelRepository orderReadModelRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "shipping-completed", groupId = "order-query")
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            Long orderId = jsonNode.get("orderId").asLong();
            Long shippingOrderId = jsonNode.get("shippingOrderId").asLong();

            // Busca a order no banco de leitura
            OrderReadModel order = orderReadModelRepository.findByOrderId(orderId);
            if (order != null) {
                // Monta ShippingInfoReadModel a partir do evento
                JsonNode shippingInfo = jsonNode.get("shippingInfo");
                ShippingInfoReadModel shipping = new ShippingInfoReadModel();
                shipping.setShippingOrderId(shippingOrderId);
                shipping.setFirstName(shippingInfo.get("firstName").asText());
                shipping.setLastName(shippingInfo.get("lastName").asText());
                shipping.setAddress(shippingInfo.get("address").asText());
                shipping.setCity(shippingInfo.get("city").asText());
                shipping.setEmail(shippingInfo.get("email").asText());
                shipping.setPostalCode(shippingInfo.get("postal_code").asText());
                shipping.setStatus("COMPLETED");
                shipping.setOrder(order);

                // Associa shipping ao order e salva ambos
                order.setShipping(shipping);
                order.setShippingOrderId(shippingOrderId);
                order.setStatus("COMPLETED"); // <-- Adicione esta linhaAdicione esta linha para atualizar o status
                orderReadModelRepository.save(order);
                System.out.println("Order " + orderId + " atualizada com shippingOrderId " + shippingOrderId + " e status COMPLETED no banco de leitura.");
            } else {
                System.out.println("Order " + orderId + " não encontrada no banco de leitura. (eventual consistency)");
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar evento shipping-completed: " + e.getMessage());
        }
    }
}

