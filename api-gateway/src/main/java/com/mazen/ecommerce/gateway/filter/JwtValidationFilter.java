package com.mazen.ecommerce.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public JwtValidationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Get Authorization header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange);
            }

            // Call auth service to validate token
            return webClientBuilder.build()
                    .post()
                    .uri("lb://AUTH-SERVICE/api/internal/validate-token")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(TokenValidationResponse.class)
                    .flatMap(response -> {
                        if (response.getSuccess() && response.getData().isValid()) {
                            // Add user info to headers for downstream services
                            var mutatedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Id", response.getData().getUserId().toString())
                                    .header("X-User-Email", response.getData().getEmail())
                                    .header("X-User-Role", response.getData().getRole().toString())
                                    .build();

                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        } else {
                            return unauthorizedResponse(exchange);
                        }
                    })
                    .onErrorResume(error -> unauthorizedResponse(exchange));
        };
    }

    private Mono<Void> unauthorizedResponse(/*org.springframework.cloud.gateway.filter.GatewayFilterChain.ServerHttpResponse response,
                                            */ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private Mono<Void> unauthorizedResponse(org.springframework.web.server.ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }

    // Response classes for auth service
    public static class TokenValidationResponse {
        private boolean success;
        private TokenData data;

        // getters and setters
        public boolean getSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public TokenData getData() { return data; }
        public void setData(TokenData data) { this.data = data; }
    }

    public static class TokenData {
        private boolean valid;
        private Long userId;
        private String email;
        private String role;

        // getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
