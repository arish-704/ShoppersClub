package com.arish.shoppersclub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProduct(Product product);

    List<ProductImage> findByProductOrderByDisplayOrderAsc(Product product);

    Optional<ProductImage> findByIdAndProduct(Long id, Product product);

    boolean existsByProduct(Product product);

    Optional<ProductImage> findByProductAndIsPrimaryTrue(Product product);

    boolean existsByProductAndImageUrl(Product product, String imageUrl);

}
