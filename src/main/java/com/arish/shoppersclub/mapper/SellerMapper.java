package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateSellerRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerRequest;
import com.arish.shoppersclub.dto.response.SellerResponse;
import com.arish.shoppersclub.entity.Seller;

@Component
public class SellerMapper {

    public Seller toEntity(CreateSellerRequest request){
        return Seller.builder()
                     .storeName(request.storeName())
                     .description(request.description())
                     .phoneNumber(request.phoneNumber())
                     .gstNumber(request.gstNumber())
                     .build();
    }

    public void updateEntity(Seller seller , UpdateSellerRequest request){
        seller.setStoreName(request.storeName());
        seller.setDescription(request.description());
        seller.setPhoneNumber(request.phoneNumber());
        seller.setGstNumber(request.gstNumber());
    }

    public SellerResponse toResponse(Seller seller){
        return new SellerResponse(
            seller.getId(),
            seller.getStoreName(),
            seller.getDescription(),
            seller.getPhoneNumber(),
            seller.getGstNumber(),
            seller.isVerified(),
            seller.getCreatedAt()
        );
    }

}
