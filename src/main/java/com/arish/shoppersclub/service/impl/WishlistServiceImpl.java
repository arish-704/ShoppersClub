package com.arish.shoppersclub.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.AddToWishlistRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.WishlistItemResponse;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.entity.WishlistItem;
import com.arish.shoppersclub.exception.DuplicateWishlistItemException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.WishlistItemNotFoundException;
import com.arish.shoppersclub.mapper.WishlistItemMapper;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.repository.WishlistItemRepository;
import com.arish.shoppersclub.service.WishlistService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistItemMapper wishlistItemMapper;

    @Override
    public WishlistItemResponse addToWishlist(AddToWishlistRequest request) {
        User user = getAuthenticatedUser();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.productId()));

        if (wishlistItemRepository.existsByUserAndProduct(user, product)) {
            throw new DuplicateWishlistItemException("Product '" + product.getName() + "' is already in your wishlist");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);
        return wishlistItemMapper.toResponse(savedItem);
    }

    @Override
    public PagedResponse<WishlistItemResponse> getMyWishlist(int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = getAuthenticatedUser();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<WishlistItem> itemPage = wishlistItemRepository.findByUser(user, pageable);

        List<WishlistItemResponse> content = itemPage.getContent().stream()
                .map(wishlistItemMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                itemPage.getNumber(),
                itemPage.getSize(),
                itemPage.getTotalElements(),
                itemPage.getTotalPages(),
                itemPage.isLast()
        );
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = getAuthenticatedUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        WishlistItem wishlistItem = wishlistItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new WishlistItemNotFoundException("Wishlist item not found for product id: " + productId));

        wishlistItemRepository.delete(wishlistItem);
    }

    @Override
    @Transactional
    public void clearWishlist() {
        User user = getAuthenticatedUser();
        wishlistItemRepository.deleteByUser(user);
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pageNo, pageSize, sort);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
