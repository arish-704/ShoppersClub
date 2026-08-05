package com.arish.shoppersclub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arish.shoppersclub.dto.request.CreateReviewRequest;
import com.arish.shoppersclub.dto.request.UpdateReviewRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductRatingSummaryResponse;
import com.arish.shoppersclub.dto.response.ReviewResponse;
import com.arish.shoppersclub.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/v1/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {

        ReviewResponse response = reviewService.createReview(productId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/v1/products/{productId}/reviews")
    public ResponseEntity<PagedResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ReviewResponse> response = reviewService.getProductReviews(productId, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/products/{productId}/reviews/summary")
    public ResponseEntity<ProductRatingSummaryResponse> getReviewSummary(
            @PathVariable Long productId) {

        ProductRatingSummaryResponse response = reviewService.getReviewSummary(productId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/v1/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {

        ReviewResponse response = reviewService.updateReview(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/v1/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {

        reviewService.deleteReview(id);
    }
}
