package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.response.ReviewResponse;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Review;
import com.arish.shoppersclub.entity.User;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        Product product = review.getProduct();
        User user = review.getUser();

        Long productId = product != null ? product.getId() : null;
        String productName = product != null ? product.getName() : "Unknown Product";
        String reviewerFirstName = user != null ? user.getFirstName() : "Anonymous";

        return new ReviewResponse(
                review.getId(),
                productId,
                productName,
                reviewerFirstName,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
