package com.arish.shoppersclub.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateReviewRequest;
import com.arish.shoppersclub.dto.request.UpdateReviewRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductRatingSummaryResponse;
import com.arish.shoppersclub.dto.response.ReviewResponse;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Review;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.exception.DuplicateReviewException;
import com.arish.shoppersclub.exception.NotVerifiedBuyerException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.ReviewNotFoundException;
import com.arish.shoppersclub.mapper.ReviewMapper;
import com.arish.shoppersclub.repository.OrderRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.ReviewRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(Long productId, CreateReviewRequest request) {
        User user = getAuthenticatedUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        boolean isVerifiedBuyer = orderRepository.existsByUserAndProductAndOrderStatus(user, product, OrderStatus.DELIVERED);
        if (!isVerifiedBuyer) {
            throw new NotVerifiedBuyerException("Only verified buyers who have received a delivered order for this product can write a review");
        }

        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new DuplicateReviewException("You have already submitted a review for this product");
        }

        Review review = Review.builder()
                .rating(request.rating())
                .comment(request.comment())
                .user(user)
                .product(product)
                .build();

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public PagedResponse<ReviewResponse> getProductReviews(Long productId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Review> reviewPage = reviewRepository.findByProduct(product, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(reviewMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    @Override
    public ProductRatingSummaryResponse getReviewSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        Double rawAvg = reviewRepository.calculateAverageRating(product);
        double averageRating = 0.0;
        if (rawAvg != null) {
            averageRating = BigDecimal.valueOf(rawAvg).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        Long totalReviews = reviewRepository.countByProduct(product);

        return new ProductRatingSummaryResponse(productId, averageRating, totalReviews);
    }

    @Override
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request) {
        User user = getAuthenticatedUser();

        Review review = reviewRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        review.setRating(request.rating());
        review.setComment(request.comment());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long id) {
        User user = getAuthenticatedUser();

        Review review = reviewRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        reviewRepository.delete(review);
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pageNo, pageSize, sort);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
