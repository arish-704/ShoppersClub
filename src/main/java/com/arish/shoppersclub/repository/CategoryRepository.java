package com.arish.shoppersclub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.shoppersclub.entity.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category , Long> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    List<Category> findByActiveTrue();

}
