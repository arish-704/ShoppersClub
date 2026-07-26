package com.arish.shoppersclub.service;

import java.util.List;

import com.arish.shoppersclub.dto.request.CreateAddressRequest;
import com.arish.shoppersclub.dto.request.UpdateAddressRequest;
import com.arish.shoppersclub.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse createAddress(CreateAddressRequest request);
    List<AddressResponse> getMyAddress();
    AddressResponse getAddressById(Long id);
    AddressResponse updateAddress(Long id, UpdateAddressRequest request);
    void deleteAddressById(Long id);

}
