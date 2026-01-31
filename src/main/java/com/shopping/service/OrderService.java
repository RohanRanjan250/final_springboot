package com.shopping.service;

import com.shopping.dto.CreateOrderRequest;
import com.shopping.dto.OrderDTO;
import com.shopping.exception.BadRequestException;
import com.shopping.exception.ResourceNotFoundException;
import com.shopping.model.*;
import com.shopping.repository.CartRepository;
import com.shopping.repository.OrderRepository;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    
    @Transactional
    public OrderDTO createOrder(User user, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        
        // Validate stock and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            
            BigDecimal price = product.getDiscountPrice() != null ? 
                    product.getDiscountPrice() : product.getPrice();
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentId(request.getPaymentId());
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderItems(new ArrayList<>());
        
        // Create order items and update stock
        for (var cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            
            BigDecimal price = product.getDiscountPrice() != null ? 
                    product.getDiscountPrice() : product.getPrice();
            orderItem.setPriceAtPurchase(price);
            
            order.getOrderItems().add(orderItem);
            
            // Update stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        order = orderRepository.save(order);
        
        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);
        
        // Send confirmation email
        String emailBody = buildOrderConfirmationEmail(order, user);
        emailService.sendOrderConfirmation(user.getEmail(), emailBody);
        
        return convertToDTO(order);
    }
    
    public OrderDTO getOrderById(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to view this order");
        }
        
        return convertToDTO(order);
    }
    
    public Page<OrderDTO> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable).map(this::convertToDTO);
    }
    
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        order = orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    @Transactional
    public OrderDTO cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to cancel this order");
        }
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel delivered order");
        }
        
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }
        
        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentId(order.getPaymentId());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        var items = new ArrayList<OrderDTO.OrderItemDTO>();
        for (OrderItem item : order.getOrderItems()) {
            OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtPurchase(item.getPriceAtPurchase());
            itemDTO.setSubtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
            items.add(itemDTO);
        }
        dto.setItems(items);
        
        return dto;
    }
    
    private String buildOrderConfirmationEmail(Order order, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getFirstName()).append(" ").append(user.getLastName()).append(",\n\n");
        sb.append("Thank you for your order!\n\n");
        sb.append("Order ID: ").append(order.getId()).append("\n");
        sb.append("Total Amount: ₹").append(order.getTotalAmount()).append("\n");
        sb.append("Payment ID: ").append(order.getPaymentId()).append("\n");
        sb.append("Shipping Address: ").append(order.getShippingAddress()).append("\n\n");
        sb.append("Order Items:\n");
        
        for (OrderItem item : order.getOrderItems()) {
            sb.append("- ").append(item.getProduct().getName())
              .append(" x ").append(item.getQuantity())
              .append(" = ₹").append(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
              .append("\n");
        }
        
        sb.append("\nWe will notify you once your order is shipped.\n\n");
        sb.append("Best regards,\nOnline Shopping Team");
        
        return sb.toString();
    }
}
