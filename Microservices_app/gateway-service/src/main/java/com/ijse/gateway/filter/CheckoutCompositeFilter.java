package com.ijse.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CheckoutCompositeFilter implements GlobalFilter, Ordered {

  private final WebClient webClient;

  public CheckoutCompositeFilter(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (!exchange.getRequest().getPath().toString().contains("/gateway/checkout")) {
      return chain.filter(exchange);
    }

    String username = exchange.getRequest().getHeaders().getFirst("X-User");
    if (username == null) {
      return sendJson(exchange, 401, "{ \"status\": \"fail\", \"message\": \"Unauthorized\" }");
    }

    return exchange.getRequest().getBody().collectList().flatMap(buffers -> {
      // monta o JSON de shippingInfo
      StringBuilder sb = new StringBuilder();
      buffers.forEach(buf -> {
        byte[] bytes = new byte[buf.readableByteCount()];
        buf.read(bytes);
        sb.append(new String(bytes, StandardCharsets.UTF_8));
      });
      String shippingInfoJson = sb.toString();

      // chama o Order-Service via HTTP
      return webClient.post()
          .uri("http://order-service:8083/orders/checkout")
          .header("X-User", username)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(shippingInfoJson)
          .retrieve()
          .toBodilessEntity()
          .flatMap(resp ->
            sendJson(exchange, 202, "{ \"status\": \"checkout initiated\" }")
          );
    });
  }

  private Mono<Void> sendJson(ServerWebExchange ex, int status, String body) {
    ServerHttpResponse r = ex.getResponse();
    r.setStatusCode(HttpStatus.valueOf(status));
    r.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    return r.writeWith(Mono.just(r.bufferFactory().wrap(body.getBytes())));
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
