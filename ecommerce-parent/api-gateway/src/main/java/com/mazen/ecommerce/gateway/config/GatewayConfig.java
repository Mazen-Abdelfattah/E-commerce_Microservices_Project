package com.mazen.ecommerce.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@LoadBalancerClient(name = "wallet-service")
//@LoadBalancerClient(name = "shop-service")
//@LoadBalancerClient(name = "inventory-service")
public class GatewayConfig {

//    @Bean
//    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // Wallet Service
//                .route("wallet-service", r -> r
//                        .path("/api/wallets/**")
////                        .filters(f -> f.stripPrefix(2)) // Remove /api/wallets
//                        .uri("lb://WALLET-SERVICE"))
//
//                // Inventory Service
//                .route("inventory-service", r -> r
//                        .path("/api/products/**")
////                        .filters(f -> f.stripPrefix(2)) // Remove /api/products
//                        .uri("lb://INVENTORY-SERVICE"))
//
//                // Shop Service (orders, cart, shop)
//                .route("shop-service", r -> r
//                        .path("/api/users/**") //TODO: Might be needed to change, waiting till adding auth and changing endpoints
////                        .filters(f -> f.stripPrefix(2)) // Remove /api/shop
//                        .uri("lb://SHOP-SERVICE"))
//                // Health check route for all services
//                .route("health-check", r -> r
//                        .path("/actuator/health/**")
//                        .uri("lb://eureka-server"))
//                .build();
//    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Wallet Service Routes with Load Balancing
                .route("wallet-service", r -> r
                        .path("/api/wallets/**")
                        .uri("lb://WALLET-SERVICE"))

                // Shop Service Routes with Load Balancing
                .route("shop-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://SHOP-SERVICE"))

                // Inventory Service Routes with Load Balancing
                .route("inventory-service", r -> r
                        .path("/api/products/**")
                        .uri("lb://INVENTORY-SERVICE"))

                .build();
    }
}
