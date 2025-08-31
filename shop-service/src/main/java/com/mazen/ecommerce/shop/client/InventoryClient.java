package com.mazen.ecommerce.shop.client;

import com.mazen.ecommerce.shop.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @GetMapping("/api/inventory/{sku}/stock")
    Boolean isInStock(@PathVariable("sku") String sku, @RequestParam("quantity") int quantity);

    @PostMapping("/api/inventory/{sku}/decrease")
    Void decreaseStock(@PathVariable("sku") String sku, @RequestParam("quantity") int quantity);

    @GetMapping("/api/inventory/{sku}")
    ProductResponse getProductBySku(@PathVariable("sku") String sku);

    @PostMapping("/api/inventory/{sku}/increase")
    Void increaseStock(@PathVariable("sku") String sku,@RequestParam("newQuantity") Integer quantity);
}
