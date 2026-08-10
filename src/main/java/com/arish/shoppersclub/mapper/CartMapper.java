package com.arish.shoppersclub.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.response.CartItemResponse;
import com.arish.shoppersclub.dto.response.CartResponse;
import com.arish.shoppersclub.entity.Cart;
import com.arish.shoppersclub.entity.CartItem;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.ProductImage;
import com.arish.shoppersclub.repository.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductImageRepository productImageRepository;

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal unitPrice = product != null ? product.getPrice() : BigDecimal.ZERO;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        Long productId = product != null ? product.getId() : null;
        String productName = product != null ? product.getName() : "Unknown Product";

        String productImageUrl = null;
        if (product != null) {
            productImageUrl = productImageRepository.findByProductAndIsPrimaryTrue(product)
                    .map(ProductImage::getImageUrl)
                    .orElseGet(() -> productImageRepository.findByProduct(product).stream()
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(null));
        }

        return new CartItemResponse(
                cartItem.getId(),
                productId,
                productName,
                productImageUrl,
                unitPrice,
                cartItem.getQuantity(),
                subtotal
        );
    }

    public CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, Integer totalItems, BigDecimal totalAmount) {
        return new CartResponse(
                cart.getId(),
                items,
                totalItems,
                totalAmount
        );
    }
}
