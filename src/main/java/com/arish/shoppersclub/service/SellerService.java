package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.CreateSellerRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerRequest;
import com.arish.shoppersclub.dto.response.SellerResponse;

public interface SellerService {
    SellerResponse createSeller(CreateSellerRequest request);
    SellerResponse getMySeller();
    SellerResponse updateSeller(UpdateSellerRequest request);
    void deleteSeller();
}
