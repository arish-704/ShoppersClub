package com.arish.shoppersclub.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateSellerRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerRequest;
import com.arish.shoppersclub.dto.response.SellerResponse;
import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.exception.SellerAlreadyExistsException;
import com.arish.shoppersclub.exception.SellerNotFoundException;
import com.arish.shoppersclub.exception.StoreNameAlreadyExistsException;
import com.arish.shoppersclub.mapper.SellerMapper;
import com.arish.shoppersclub.repository.SellerRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.SellerService;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;
    private final UserRepository userRepository;


    @Override
    public SellerResponse createSeller(CreateSellerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if(sellerRepository.existsByUser(user)){
            throw new SellerAlreadyExistsException("Seller profile already exists for this user.");
        }
        if(sellerRepository.existsByStoreName(request.storeName())){
            throw new StoreNameAlreadyExistsException("Store name already exists.");

        }
        Seller seller = sellerMapper.toEntity(request);
        seller.setUser(user);
        seller.setVerified(false);
        Seller savedSeller = sellerRepository.save(seller);
        return sellerMapper.toResponse(savedSeller);          
    }


    @Override
    public SellerResponse getMySeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Seller seller = sellerRepository.findByUser(user).orElseThrow(() -> new SellerNotFoundException("This Seller does not exist"));
        return sellerMapper.toResponse(seller);
    }


    @Override
    public SellerResponse updateSeller(UpdateSellerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Seller seller = sellerRepository.findByUser(user).orElseThrow(() -> new SellerNotFoundException("This Seller does not exist"));

        // Check if the store name is being changed.
        // If changed, verify the new store name isn't already taken by another seller to prevent unique constraint violations.
        // If unchanged, skip validation so the seller doesn't trigger a duplicate conflict against their own store name.
        if (!seller.getStoreName().equals(request.storeName())) {
            if (sellerRepository.existsByStoreName(request.storeName())) {
                throw new StoreNameAlreadyExistsException("Store name already exists.");
            }
        }
        sellerMapper.updateEntity(seller, request);
        Seller updatedSeller = sellerRepository.save(seller);
        return sellerMapper.toResponse(updatedSeller);
    }


    @Override
    public void deleteSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Seller seller = sellerRepository.findByUser(user)
                                        .orElseThrow(() -> new SellerNotFoundException("Seller profile not found."));
        sellerRepository.delete(seller);
    }

    

}
