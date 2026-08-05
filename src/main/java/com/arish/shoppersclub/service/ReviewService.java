package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.CreateReviewRequest;
import com.arish.shoppersclub.dto.request.UpdateReviewRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductRatingSummaryResponse;
import com.arish.shoppersclub.dto.response.ReviewResponse;

public interface ReviewService {

    ReviewResponse createReview(Long productId, CreateReviewRequest request);

    PagedResponse<ReviewResponse> getProductReviews(Long productId, int pageNo, int pageSize, String sortBy, String sortDir);

    ProductRatingSummaryResponse getReviewSummary(Long productId);

    ReviewResponse updateReview(Long id, UpdateReviewRequest request);

    void deleteReview(Long id);

}
