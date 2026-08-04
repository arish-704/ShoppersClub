package com.arish.shoppersclub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Category;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.enums.ProductStatus;

/**
 * ============================================================================
 * ProductRepository - Pagination Concepts
 * ============================================================================
 *
 * Spring Data JPA Pagination Feature:
 * - When a method accepts org.springframework.data.domain.Pageable as a parameter,
 *   Spring Data JPA automatically constructs a SQL query with database-level
 *   LIMIT and OFFSET clauses.
 *
 * - Instead of returning java.util.List, returning org.springframework.data.domain.Page<Product>
 *   executes TWO queries behind the scenes:
 *   1. SELECT ... LIMIT pageSize OFFSET pageNumber * pageSize (Fetches current page rows)
 *   2. SELECT COUNT(...) (Fetches total row count to compute total pages)
 * ============================================================================
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySeller(Seller seller, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Optional<Product> findByIdAndSeller(Long id, Seller seller);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

}
