package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.AddToWishlistRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.WishlistItemResponse;

public interface WishlistService {

    WishlistItemResponse addToWishlist(AddToWishlistRequest request);

    PagedResponse<WishlistItemResponse> getMyWishlist(int pageNo, int pageSize, String sortBy, String sortDir);

    void removeFromWishlist(Long productId);

    void clearWishlist();

}
