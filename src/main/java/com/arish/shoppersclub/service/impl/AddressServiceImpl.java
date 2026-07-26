package com.arish.shoppersclub.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateAddressRequest;
import com.arish.shoppersclub.dto.request.UpdateAddressRequest;
import com.arish.shoppersclub.dto.response.AddressResponse;
import com.arish.shoppersclub.entity.Address;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.mapper.AddressMapper;
import com.arish.shoppersclub.repository.AddressRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.AddressService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressResponse createAddress(CreateAddressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressMapper.toEntity(request);
        address.setUser(user); // This address belongs to this user
        boolean hasAddresses = addressRepository.existsByUser(user); // checking if the user has any addresses saved or not
        address.setDefaultAddress(!hasAddresses); // setting the found address as default if it is the very first address
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getMyAddress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                 .map(addressMapper::toResponse)
                 .toList();
        }

    @Override
    public AddressResponse getAddressById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository
                                .findByIdAndUser(id, user)
                                .orElseThrow(() -> new RuntimeException("Address not found"));
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long id, UpdateAddressRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAddress'");
    }

    @Override
    public void deleteAddressById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAddressById'");
    }

}
