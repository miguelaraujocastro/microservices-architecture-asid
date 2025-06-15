package com.ijse.gateway.filter;

import com.ijse.gateway.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // -2147483648, a mais alta
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("➡ JwtAuthenticationFilter foi acionado!");

        String path = exchange.getRequest().getURI().getPath();

        // ⚠️ Permitir livremente os endpoints públicos
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            System.out.println("➡ JwtAuthenticationFilter ATIVO para path: " + path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtils.validateJwtToken(token)) {
            return unauthorized(exchange);
        }

        String username = jwtUtils.getUsernameFromJwtToken(token);

        // Injeta header X-User para os microserviços
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User", username)
                        .build())
                .build();

        return chain.filter(modifiedExchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
