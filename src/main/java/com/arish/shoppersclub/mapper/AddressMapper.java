package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateAddressRequest;
import com.arish.shoppersclub.dto.request.UpdateAddressRequest;
import com.arish.shoppersclub.dto.response.AddressResponse;
import com.arish.shoppersclub.entity.Address;

@Component
public class AddressMapper {

    public Address toEntity(CreateAddressRequest request){
        return Address.builder()
                      .fullName(request.fullName())
                      .phoneNumber(request.phoneNumber())
                      .addressLine1(request.addressLine1())
                      .addressLine2(request.addressLine2())
                      .city(request.city())
                      .state(request.state())
                      .country(request.country())
                      .postalCode(request.postalCode())
                      .addressType(request.addressType())
                      .build();
    }

    public void updateEntity(Address address, UpdateAddressRequest request) {
        address.setFullName(request.fullName());
        address.setPhoneNumber(request.phoneNumber());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setPostalCode(request.postalCode());
        address.setAddressType(request.addressType());
    }

    public AddressResponse toResponse(Address address){
        return new AddressResponse(
            address.getId(),
            address.getFullName(),
            address.getPhoneNumber(),
            address.getAddressLine1(),
            address.getAddressLine2(),
            address.getCity(),
            address.getState(),
            address.getCountry(),
            address.getPostalCode(),
            address.getAddressType(),
            address.isDefaultAddress(),
            address.getCreatedAt()
        );
    }

}
