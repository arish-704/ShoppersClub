package com.arish.shoppersclub.entity;

import java.math.BigDecimal;

import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_order_user_status", columnList = "user_id, order_status")
    }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @NotBlank
    private String fullName;

    @Column(nullable = false)
    @NotBlank
    private String phoneNumber;

    @Column(nullable = false)
    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    @NotBlank
    private String city;

    @Column(nullable = false)
    @NotBlank
    private String state;

    @Column(nullable = false)
    @NotBlank
    private String country;

    @Column(nullable = false)
    @NotBlank
    private String postalCode;

    @Column(nullable = false)
    @NotNull
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @NotNull
    private Integer totalItems;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;

}
