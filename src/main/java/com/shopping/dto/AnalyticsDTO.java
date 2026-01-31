package com.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalUsers;
    private Long totalProducts;
    private List<TopProductDTO> topProducts;
    private List<RevenueByDateDTO> revenueByDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private Long orderCount;
        private BigDecimal revenue;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByDateDTO {
        private String date;
        private BigDecimal revenue;
        private Long orderCount;
    }
}
