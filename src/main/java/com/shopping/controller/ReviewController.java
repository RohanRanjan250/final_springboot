package com.shopping.controller;

import com.shopping.dto.ReviewDTO;
import com.shopping.model.User;
import com.shopping.service.ReviewService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product review endpoints")
public class ReviewController {
    
    private final ReviewService reviewService;
    private final UserService userService;
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<Page<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }
    
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create a product review")
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(reviewService.createReview(user, reviewDTO));
    }
    
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update a review")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDTO reviewDTO,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(reviewService.updateReview(id, user, reviewDTO));
    }
    
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        reviewService.deleteReview(id, user);
        return ResponseEntity.noContent().build();
    }
}
