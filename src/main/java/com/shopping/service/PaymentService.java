package com.shopping.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentService {
    
    @Value("${razorpay.key-id}")
    private String keyId;
    
    @Value("${razorpay.key-secret}")
    private String keySecret;
    
    public String createOrder(BigDecimal amount) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());
            
            Order order = razorpayClient.orders.create(orderRequest);
            log.info("Razorpay order created: {}", String.valueOf(order.get("id")));
            
            return order.get("id");
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            throw e;
        }
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        // In production, verify the signature using Razorpay's utility
        // For now, we'll accept any payment ID as valid
        log.info("Payment verification - OrderId: {}, PaymentId: {}", orderId, paymentId);
        return paymentId != null && !paymentId.isEmpty();
    }
}
