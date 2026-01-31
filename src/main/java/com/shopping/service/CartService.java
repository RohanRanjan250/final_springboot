package com.shopping.service;

import com.shopping.dto.CartDTO;
import com.shopping.exception.BadRequestException;
import com.shopping.exception.ResourceNotFoundException;
import com.shopping.model.Cart;
import com.shopping.model.CartItem;
import com.shopping.model.Product;
import com.shopping.model.User;
import com.shopping.repository.CartRepository;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    public CartDTO getCartByUser(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCart(user));
        return convertToDTO(cart);
    }
    
    @Transactional
    public CartDTO addItemToCart(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCart(user));
        
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }
        
        cart = cartRepository.save(cart);
        return convertToDTO(cart);
    }
    
    @Transactional
    public CartDTO updateCartItem(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
        
        item.setQuantity(quantity);
        cart = cartRepository.save(cart);
        return convertToDTO(cart);
    }
    
    @Transactional
    public CartDTO removeItemFromCart(User user, Long productId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cart = cartRepository.save(cart);
        return convertToDTO(cart);
    }
    
    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }
    
    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        var items = new ArrayList<CartDTO.CartItemDTO>();
        
        for (CartItem item : cart.getItems()) {
            CartDTO.CartItemDTO itemDTO = new CartDTO.CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            
            BigDecimal price = item.getProduct().getDiscountPrice() != null ? 
                    item.getProduct().getDiscountPrice() : item.getProduct().getPrice();
            itemDTO.setPrice(price);
            itemDTO.setQuantity(item.getQuantity());
            
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemDTO.setSubtotal(subtotal);
            itemDTO.setImageUrl(item.getProduct().getImageUrl());
            
            items.add(itemDTO);
            totalAmount = totalAmount.add(subtotal);
        }
        
        dto.setItems(items);
        dto.setTotalAmount(totalAmount);
        return dto;
    }
}
