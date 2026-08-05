package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.AddToCartRequest;
import com.arish.shoppersclub.dto.request.UpdateCartItemRequest;
import com.arish.shoppersclub.dto.response.CartResponse;

public interface CartService {

    CartResponse addItemToCart(AddToCartRequest request);

    CartResponse getMyCart();

    CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long itemId);

    void clearCart();

}
