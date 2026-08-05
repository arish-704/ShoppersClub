package com.arish.shoppersclub.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.AddToCartRequest;
import com.arish.shoppersclub.dto.request.UpdateCartItemRequest;
import com.arish.shoppersclub.dto.response.CartItemResponse;
import com.arish.shoppersclub.dto.response.CartResponse;
import com.arish.shoppersclub.entity.Cart;
import com.arish.shoppersclub.entity.CartItem;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.ProductStatus;
import com.arish.shoppersclub.exception.CartItemNotFoundException;
import com.arish.shoppersclub.exception.InsufficientStockException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.ProductUnavailableException;
import com.arish.shoppersclub.mapper.CartMapper;
import com.arish.shoppersclub.repository.CartItemRepository;
import com.arish.shoppersclub.repository.CartRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse addItemToCart(AddToCartRequest request) {
        Cart cart = getOrCreateCartForAuthenticatedUser();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.productId()));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ProductUnavailableException("Product '" + product.getName() + "' is currently unavailable for purchase");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {
            int mergedQuantity = cartItem.getQuantity() + request.quantity();
            if (mergedQuantity > product.getStock()) {
                throw new InsufficientStockException("Cannot add item. Requested quantity (" + mergedQuantity + ") exceeds available stock (" + product.getStock() + ")");
            }
            cartItem.setQuantity(mergedQuantity);
            cartItemRepository.save(cartItem);
        } else {
            if (request.quantity() > product.getStock()) {
                throw new InsufficientStockException("Cannot add item. Requested quantity (" + request.quantity() + ") exceeds available stock (" + product.getStock() + ")");
            }
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse getMyCart() {
        Cart cart = getOrCreateCartForAuthenticatedUser();
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCartForAuthenticatedUser();

        CartItem cartItem = cartItemRepository.findByIdAndCart(itemId, cart)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + itemId));

        if (request.quantity() == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            Product product = cartItem.getProduct();
            if (request.quantity() > product.getStock()) {
                throw new InsufficientStockException("Requested quantity (" + request.quantity() + ") exceeds available stock (" + product.getStock() + ")");
            }
            cartItem.setQuantity(request.quantity());
            cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long itemId) {
        Cart cart = getOrCreateCartForAuthenticatedUser();

        CartItem cartItem = cartItemRepository.findByIdAndCart(itemId, cart)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + itemId));

        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCartForAuthenticatedUser();
        cartItemRepository.deleteByCart(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(cartMapper::toCartItemResponse)
                .toList();

        int totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return cartMapper.toCartResponse(cart, itemResponses, totalItems, totalAmount);
    }

    private Cart getOrCreateCartForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}
