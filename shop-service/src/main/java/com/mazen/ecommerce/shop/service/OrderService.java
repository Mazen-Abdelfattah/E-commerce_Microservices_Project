package com.mazen.ecommerce.shop.service;


import com.mazen.ecommerce.shop.client.InventoryClient;
import com.mazen.ecommerce.shop.client.WalletClient;
import com.mazen.ecommerce.shop.client.dto.*;
import com.mazen.ecommerce.shop.dto.order.CreateOrderRequest;
import com.mazen.ecommerce.shop.dto.order.OrderItemResponse;
import com.mazen.ecommerce.shop.dto.order.OrderResponse;
import com.mazen.ecommerce.shop.dto.payment.PaymentResponse;
import com.mazen.ecommerce.shop.model.*;
import com.mazen.ecommerce.shop.model.enums.OrderStatus;
import com.mazen.ecommerce.shop.model.enums.PaymentStatus;
import com.mazen.ecommerce.shop.repository.CartRepository;
import com.mazen.ecommerce.shop.repository.OrderRepository;
import com.mazen.ecommerce.shop.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final WalletClient walletClient;
    private final InventoryClient inventoryClient;
//    private final InventoryServiceAdapter inventoryServiceAdapter;
//    private final WalletServiceAdapter walletServiceAdapter;

    public OrderResponse createOrder(Long userId, CreateOrderRequest request) throws IllegalAccessException {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + request.getCartId()));

        if (!cart.getUserId().equals(userId)) {
            throw new IllegalAccessException("Cart does not belong to user: " + userId);
        }

        if (cart.getCartItems().isEmpty()) {
            throw new EntityNotFoundException("Cannot create order from empty cart");
        }

        // Check inventory before placing order
        for (CartItem cartItem : cart.getCartItems()) {
//            boolean inStock = inventoryClient.isInStock(cartItem.getSku(), cartItem.getQuantity());
            boolean inStock = isInStockSafe(cartItem.getSku(), cartItem.getQuantity());
            if (!inStock) {
                //TODO See if it is possible to return JSON instead
                throw new IllegalStateException("Product " + cartItem.getProductName() + " is out of stock!");
            }
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        Order finalOrder = order;
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(ci -> OrderItem.builder()
//                        .order(order)
                        .order(finalOrder)
                        .sku(ci.getSku())
                        .productName(ci.getProductName())
                        .quantity(ci.getQuantity())
                        .priceAtPurchase(ci.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        /*
         *  'orderItemRepository.saveAll(orderItems);' WILL MAKE ERROR
         *  Save the order instead of saving orderItems (this will cascade save the items)
         *  Don't Forget: U used 'cascade = CascadeType.ALL' and 'orphanRemoval = true' in model
         */
        order = orderRepository.save(order);


        // After saving order → decrease stock
        for (CartItem cartItem : cart.getCartItems()) {
//            inventoryClient.decreaseStock(cartItem.getSku(), cartItem.getQuantity());
            decreaseStockSafe(cartItem.getSku(), cartItem.getQuantity());
        }


        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();



        List<WalletResponse> wallets = getWallets(userId); // resilience wrapped
        if (wallets.isEmpty()) {
            throw new IllegalStateException("No wallets available for user " + userId);
        }

        // TODO Make it take the wallet that the user choose (Not just the first wallet)
        TransactionResponse trx = withdrawFromWallet(wallets.get(0).getId(), totalAmount);
        if (trx == null) {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);

            // ROLLBACK: restore inventory
            for (CartItem cartItem : cart.getCartItems()) {
//                inventoryClient.increaseStock(cartItem.getSku(), cartItem.getQuantity());
                increaseStockSafe(cartItem.getSku(), cartItem.getQuantity());
            }

        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.SHIPPED); //TODO Make it paid (NOTE: if the user wanna cancel, the money must get pack to his wallet)
        }

        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);


        cartRepository.delete(cart);

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        //TODO Check if the User Exists via Feign Client

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new OrderCancellationException("Cannot cancel shipped order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderCancellationException("Order is already cancelled");
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);

        // Update payment status if exists
        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.FAILED);
            paymentRepository.save(order.getPayment());
        }

        order = orderRepository.save(order);
        return toResponse(order);
    }



    // ====================== HELPER METHODS ======================

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class OrderCancellationException extends RuntimeException {
        public OrderCancellationException(String message) {
            super(message);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Integer totalItems = itemResponses.stream()
                .mapToInt(OrderItemResponse::getQuantity)
                .sum();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(itemResponses)
                .payment(order.getPayment() != null ? toResponse(order.getPayment()) : null)
                .createdAt(order.getCreatedAt())
                .totalItems(totalItems)
                .build();
    }

    private OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .sku(orderItem.getSku())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subtotal(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .build();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .paidAt(payment.getCreatedAt())
                .transactionId(payment.getTransactionId())
                .build();
    }

    // ====================== RESILIENCE METHODS ======================
    // TODO: Can be moved into a separate class later (WalletServiceAdapter)


    // Wrap Feign call with resilience annotations
    @CircuitBreaker(name = "walletService", fallbackMethod = "walletFallback")
    @Retry(name = "walletService")
    public List<WalletResponse> getWallets(Long userId) {
        return walletClient.getUserWallet(userId);
    }

    @Retry(name = "walletService")
    @CircuitBreaker(name = "walletService", fallbackMethod = "withdrawFallback")
    public TransactionResponse withdrawFromWallet(Long walletId, BigDecimal amount) {
        return walletClient.withdraw(walletId,
                new CreateTransactionRequest(TransactionType.WITHDRAW, amount));
    }

    // === INVENTORY RESILIENCE WRAPPERS ===

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "isInStockFallback")
    @Retry(name = "inventoryService")
    public boolean isInStockSafe(String sku, int quantity) {
        return inventoryClient.isInStock(sku, quantity);
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "decreaseFallback")
    @Retry(name = "inventoryService")
    public void decreaseStockSafe(String sku, int quantity) {
        inventoryClient.decreaseStock(sku, quantity);
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "increaseFallback")
    @Retry(name = "inventoryService")
    public void increaseStockSafe(String sku, int quantity) {
        inventoryClient.increaseStock(sku, quantity);
    }


    // === Fallbacks ===
    public List<WalletResponse> walletFallback(Long userId, Throwable t) {
        log.error("Wallet service unavailable for user {}", userId, t);
        log.error("Fallback triggered: forcing order cancellation");
        return List.of(); // empty list
    }

    public TransactionResponse withdrawFallback(Long walletId, BigDecimal amount, Throwable t) {
        log.error("Wallet service unavailable, cannot withdraw from wallet {}", walletId, t);
        return null; // gracefully handle
    }

    public boolean isInStockFallback(String sku, int quantity, Throwable t) {
        log.error("Inventory unavailable for SKU {}, defaulting to false", sku, t);
        return false; // treat as out of stock
    }

    public void decreaseFallback(String sku, int quantity, Throwable t) {
        log.error("Failed to decrease stock for SKU {}", sku, t);
        // fallback: do nothing → order creation will fail later
    }

    public void increaseFallback(String sku, int quantity, Throwable t) {
        log.error("Failed to increase stock for SKU {}", sku, t);
        // fallback: log only, manual reconciliation may be needed
    }

}
