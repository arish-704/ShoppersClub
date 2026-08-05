package com.arish.shoppersclub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Review;
import com.arish.shoppersclub.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProduct(Product product, Pageable pageable);

    Optional<Review> findByIdAndUser(Long id, User user);

    boolean existsByUserAndProduct(User user, Product product);

    Long countByProduct(Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double calculateAverageRating(@Param("product") Product product);

}
