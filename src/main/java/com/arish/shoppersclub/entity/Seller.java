package com.arish.shoppersclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Seller extends BaseEntity {

    @NotBlank(message = "Store name is required")
    @Column(nullable = false, unique = true)
    private String storeName;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @Column(unique = true)
    private String gstNumber;

    @Builder.Default
    @Column(nullable = false)
    private boolean verified = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}
