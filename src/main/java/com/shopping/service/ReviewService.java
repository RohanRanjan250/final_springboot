package com.shopping.service;

import com.shopping.dto.ReviewDTO;
import com.shopping.exception.BadRequestException;
import com.shopping.exception.ResourceNotFoundException;
import com.shopping.model.Product;
import com.shopping.model.Review;
import com.shopping.model.User;
import com.shopping.repository.ProductRepository;
import com.shopping.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public ReviewDTO createReview(User user, ReviewDTO reviewDTO) {
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + reviewDTO.getProductId()));
        
        // Check if user already reviewed this product
        if (reviewRepository.findByProductIdAndUserId(reviewDTO.getProductId(), user.getId()).isPresent()) {
            throw new BadRequestException("You have already reviewed this product");
        }
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        review = reviewRepository.save(review);
        
        // Update product rating
        updateProductRating(product.getId());
        
        return convertToDTO(review);
    }
    
    public Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable).map(this::convertToDTO);
    }
    
    @Transactional
    public ReviewDTO updateReview(Long reviewId, User user, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to update this review");
        }
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        review = reviewRepository.save(review);
        
        // Update product rating
        updateProductRating(review.getProduct().getId());
        
        return convertToDTO(review);
    }
    
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to delete this review");
        }
        
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        // Update product rating
        updateProductRating(productId);
    }
    
    private void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Double avgRating = reviewRepository.getAverageRating(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);
        
        product.setRating(avgRating != null ? avgRating : 0.0);
        product.setReviewCount(reviewCount.intValue());
        
        productRepository.save(product);
    }
    
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
