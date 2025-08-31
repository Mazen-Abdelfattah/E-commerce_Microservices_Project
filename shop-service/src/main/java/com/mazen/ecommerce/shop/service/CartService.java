package com.mazen.ecommerce.shop.service;

import com.mazen.ecommerce.shop.client.InventoryClient;
import com.mazen.ecommerce.shop.client.dto.ProductResponse;
import com.mazen.ecommerce.shop.dto.cart.AddToCartRequest;
import com.mazen.ecommerce.shop.dto.cart.CartItemResponse;
import com.mazen.ecommerce.shop.dto.cart.CartResponse;
import com.mazen.ecommerce.shop.dto.cart.UpdateCartItemRequest;
import com.mazen.ecommerce.shop.model.Cart;
import com.mazen.ecommerce.shop.repository.CartRepository;
import com.mazen.ecommerce.shop.repository.CartItemRepository;
import com.mazen.ecommerce.shop.model.CartItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryClient inventoryClient;

    public CartItemResponse addItemToCart(Long userId, AddToCartRequest request) {

        Cart cart = getOrCreateCart(userId);
        cart.setCreatedAt(LocalDateTime.now());

        ProductResponse product = inventoryClient.getProductBySku(request.getSku());

        if (product == null) {
            throw new EntityNotFoundException("Product not found with SKU: " + request.getSku());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getSku().equals(request.getSku()))
                .findFirst();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update existing item quantity
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Create new cart item with mock product data
            cartItem = CartItem.builder()
                    .cart(cart)
                    .sku(product.getSku())
                    .productName(product.getName())
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            cart.getCartItems().add(cartItem);
        }

        cartItem = cartItemRepository.save(cartItem);
        cartRepository.save(cart); // Update cart timestamp

        return mapToCartItemResponse(cartItem);
    }

    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) throws IllegalAccessException {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + itemId));

        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new IllegalAccessException("Cart item does not belong to user: " + userId);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);

        // Update cart timestamp
        Cart cart = cartItem.getCart();
        cart.onUpdate();
        cart = cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    public void removeItemFromCart(Long userId, Long itemId) throws IllegalAccessException {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + itemId));

        // Verify the item belongs to the user's cart
        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new IllegalAccessException("Cart item does not belong to user: " + userId);
        }

        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        // Update cart timestamp
        cart.onUpdate();
        cartRepository.save(cart);
    }

    public CartResponse getCartByUserId(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);

        if (cartOpt.isEmpty()) {
            // Return empty cart response
            return CartResponse.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .totalAmount(BigDecimal.ZERO)
                    .itemCount(0)
                    .build();
        }

        Cart cart = cartOpt.get();
        return mapToCartResponse(cart);
    }

    public void clearCart(Long userId) {

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
            Cart cart = cartOpt.get();
            cart.getCartItems().clear();
            cart.onUpdate();
            cartRepository.delete(cart);
    }

        // ====================== HELPER METHODS ======================

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .itemCount(totalItems)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        BigDecimal itemTotal = cartItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .sku(cartItem.getSku())
                .productName(cartItem.getProductName())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .subtotal(itemTotal)
                .build();
    }

    // ====================== MOCK DATA METHODS ======================
    // TODO: Replace these with Feign client calls to inventory-service

//    private String getMockProductName(String sku) {
//        // Mock product names based on SKU
//        Map<String, String> mockProducts = Map.of(
//                "LAPTOP001", "Gaming Laptop Pro",
//                "PHONE001", "Smartphone X",
//                "BOOK001", "Java Programming Guide",
//                "SHOE001", "Running Shoes Elite"
//        );
//        return mockProducts.getOrDefault(sku, "Product " + sku);
//    }
//
//    private BigDecimal getMockUnitPrice(String sku) {
//        // Mock prices based on SKU
//        Map<String, BigDecimal> mockPrices = Map.of(
//                "LAPTOP001", new BigDecimal("999.99"),
//                "PHONE001", new BigDecimal("699.99"),
//                "BOOK001", new BigDecimal("29.99"),
//                "SHOE001", new BigDecimal("89.99")
//        );
//        return mockPrices.getOrDefault(sku, new BigDecimal("19.99"));
//    }
}
