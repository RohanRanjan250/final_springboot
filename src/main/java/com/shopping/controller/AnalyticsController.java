package com.shopping.controller;

import com.shopping.dto.AnalyticsDTO;
import com.shopping.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Analytics", description = "Analytics and reporting endpoints (Admin only)")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping
    @Operation(summary = "Get analytics dashboard data")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}
