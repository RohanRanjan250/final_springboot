package com.shopping.controller;

import com.shopping.dto.CreateOrderRequest;
import com.shopping.dto.OrderDTO;
import com.shopping.model.Order;
import com.shopping.model.User;
import com.shopping.service.OrderService;
import com.shopping.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new order from cart")
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(orderService.createOrder(user, request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(orderService.getOrderById(id, user));
    }
    
    @GetMapping
    @Operation(summary = "Get user's orders")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getUserOrders(user, pageable));
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(orderService.cancelOrder(id, user));
    }
}
