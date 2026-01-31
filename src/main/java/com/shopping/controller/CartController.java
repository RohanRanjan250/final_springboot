package com.shopping.controller;

import com.shopping.dto.CartDTO;
import com.shopping.model.User;
import com.shopping.service.CartService;
import com.shopping.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Shopping Cart", description = "Shopping cart management endpoints")
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Get user's cart")
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.getCartByUser(user));
    }
    
    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.addItemToCart(user, productId, quantity));
    }
    
    @PutMapping("/items/{productId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.updateCartItem(user, productId, quantity));
    }
    
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.removeItemFromCart(user, productId));
    }
    
    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
