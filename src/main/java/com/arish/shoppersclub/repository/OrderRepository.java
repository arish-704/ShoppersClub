package com.arish.shoppersclub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arish.shoppersclub.entity.Order;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    Optional<Order> findByIdAndUser(Long id, User user);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN OrderItem oi ON oi.order = o WHERE o.user = :user AND oi.product = :product AND o.orderStatus = :orderStatus")
    boolean existsByUserAndProductAndOrderStatus(@Param("user") User user, @Param("product") Product product, @Param("orderStatus") OrderStatus orderStatus);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus != com.arish.shoppersclub.enums.OrderStatus.CANCELLED")
    java.math.BigDecimal calculateTotalRevenue();

}
