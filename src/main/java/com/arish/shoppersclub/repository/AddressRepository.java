package com.arish.shoppersclub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Address;
import com.arish.shoppersclub.entity.User;


@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUser(User user);
    boolean existsByUser(User user);
    Optional<Address> findByIdAndUser(Long id, User user);
}
