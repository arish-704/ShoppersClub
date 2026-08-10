package com.arish.shoppersclub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.entity.User;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SellerRepository extends JpaRepository<Seller , Long> {
    boolean existsByUser(User user);
    boolean existsByStoreName(String storeName);
    Optional<Seller> findByUser(User user);
    Optional<Seller> findByStoreName(String storeName);
    Page<Seller> findByVerified(boolean verified, Pageable pageable);
}
