package com.arish.shoppersclub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.arish.shoppersclub.dto.request.CreateSellerRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerRequest;
import com.arish.shoppersclub.dto.response.SellerResponse;
import com.arish.shoppersclub.service.SellerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
@Validated
public class SellerController {

    private final SellerService sellerService;

    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(
            @Valid @RequestBody CreateSellerRequest request) {

        SellerResponse response = sellerService.createSeller(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<SellerResponse> getMySeller() {

        SellerResponse response = sellerService.getMySeller();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<SellerResponse> updateSeller(
            @Valid @RequestBody UpdateSellerRequest request) {

        SellerResponse response = sellerService.updateSeller(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteSeller() {

        sellerService.deleteSeller();

        return ResponseEntity.noContent().build();
    }
}