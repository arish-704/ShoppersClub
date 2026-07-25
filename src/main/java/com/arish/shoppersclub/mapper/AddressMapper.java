package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateAddressRequest;
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
