package com.arish.shoppersclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Address;


@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

}
