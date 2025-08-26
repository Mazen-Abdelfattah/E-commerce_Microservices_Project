package com.mazen.ecommerce.inventory.product;

import com.mazen.ecommerce.inventory.product.dto.ProductRequest;
import com.mazen.ecommerce.inventory.product.dto.ProductResponse;
import com.mazen.ecommerce.inventory.product.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest
    ){
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(
            @PathVariable String sku
    ){
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String sku,
            @Valid @RequestBody UpdateProductRequest request
            ){
        return ResponseEntity.ok(productService.updateProduct(sku, request));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String sku) {
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{sku}/stock")
    public ResponseEntity<Boolean> isInStock(
            @PathVariable String sku,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(productService.isInStock(sku, quantity));
    }

    @PostMapping("/{sku}/decrease")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable String sku,
            @RequestParam int quantity
    ) {
        productService.decreaseStock(sku, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sku}/increase")
    public ResponseEntity<Void> increaseStock(
            @PathVariable String sku,
            @RequestParam int quantity
    ) {
        productService.increaseStock(sku, quantity);
        return ResponseEntity.ok().build();
    }

}
