package com.mazen.ecommerce.shop.controller;

import com.mazen.ecommerce.shop.dto.cart.UpdateCartItemRequest;
import com.mazen.ecommerce.shop.dto.cart.AddToCartRequest;
import com.mazen.ecommerce.shop.dto.cart.CartItemResponse;
import com.mazen.ecommerce.shop.dto.cart.CartResponse;
import com.mazen.ecommerce.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") //Will be changed after making the API Gateway
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/cart/items")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request) {

        CartItemResponse response = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/cart/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) throws IllegalAccessException {

        CartResponse response = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/cart/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) throws IllegalAccessException {

        cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/cart")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {

        CartResponse response = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/cart")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {

        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
