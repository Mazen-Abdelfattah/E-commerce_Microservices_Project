//package com.mazen.ecommerce.shop.service;
//
//import com.mazen.ecommerce.shop.client.InventoryClient;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import io.github.resilience4j.retry.annotation.Retry;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Slf4j
//@Component
//public class InventoryServiceAdapter {
//    private final InventoryClient inventoryClient;
//
//    // ====================== RESILIENCE METHODS ======================
//
//    // Wrap Feign call with resilience annotations
//    // === INVENTORY RESILIENCE WRAPPERS ===
//
//    @CircuitBreaker(name = "inventoryService", fallbackMethod = "isInStockFallback")
//    @Retry(name = "inventoryService")
//    public boolean isInStockSafe(String sku, int quantity) {
//        return inventoryClient.isInStock(sku, quantity);
//    }
//
//    @CircuitBreaker(name = "inventoryService", fallbackMethod = "decreaseFallback")
//    @Retry(name = "inventoryService")
//    public void decreaseStockSafe(String sku, int quantity) {
//        inventoryClient.decreaseStock(sku, quantity);
//    }
//
//    @CircuitBreaker(name = "inventoryService", fallbackMethod = "increaseFallback")
//    @Retry(name = "inventoryService")
//    public void increaseStockSafe(String sku, int quantity) {
//        inventoryClient.increaseStock(sku, quantity);
//    }
//
//
//
//    // === Fallbacks ===
//    public boolean isInStockFallback(String sku, int quantity, Throwable t) {
//        log.error("Inventory unavailable for SKU {}, defaulting to false", sku, t);
//        return false; // treat as out of stock
//    }
//
//    public void decreaseFallback(String sku, int quantity, Throwable t) {
//        log.error("Failed to decrease stock for SKU {}", sku, t);
//        // fallback: do nothing → order creation will fail later
//    }
//
//    public void increaseFallback(String sku, int quantity, Throwable t) {
//        log.error("Failed to increase stock for SKU {}", sku, t);
//        // fallback: log only, manual reconciliation may be needed
//    }
//
//}
