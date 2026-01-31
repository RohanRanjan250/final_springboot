package com.shopping.service;

import com.shopping.dto.AnalyticsDTO;
import com.shopping.repository.OrderRepository;
import com.shopping.repository.ProductRepository;
import com.shopping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    public AnalyticsDTO getAnalytics() {
        AnalyticsDTO analytics = new AnalyticsDTO();
        
        // Total revenue
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        analytics.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // Total orders
        Long totalOrders = orderRepository.getTotalOrderCount();
        analytics.setTotalOrders(totalOrders != null ? totalOrders : 0L);
        
        // Total users
        analytics.setTotalUsers(userRepository.count());
        
        // Total products
        analytics.setTotalProducts(productRepository.count());
        
        // Revenue by date (last 30 days)
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<Object[]> revenueData = orderRepository.getRevenueByDate(startDate);
        List<AnalyticsDTO.RevenueByDateDTO> revenueByDate = new ArrayList<>();
        
        for (Object[] data : revenueData) {
            AnalyticsDTO.RevenueByDateDTO dto = new AnalyticsDTO.RevenueByDateDTO();
            dto.setDate(data[0].toString());
            dto.setRevenue((BigDecimal) data[1]);
            dto.setOrderCount(((Number) data[2]).longValue());
            revenueByDate.add(dto);
        }
        analytics.setRevenueByDate(revenueByDate);
        
        // Top products (placeholder - would need more complex query)
        analytics.setTopProducts(new ArrayList<>());
        
        return analytics;
    }
}
