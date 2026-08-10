package com.arish.shoppersclub.config;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.arish.shoppersclub.entity.Category;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.Role;
import com.arish.shoppersclub.repository.CategoryRepository;
import com.arish.shoppersclub.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Initialize default categories if empty
        if (categoryRepository.count() == 0) {
            log.info("No categories found in database. Initializing default product categories...");

            List<Category> defaultCategories = List.of(
                Category.builder().name("Electronics").description("Gadgets, Audio, Smart Devices, Accessories").active(true).build(),
                Category.builder().name("Fashion & Apparel").description("Clothing, Shoes, Underwear, Accessories").active(true).build(),
                Category.builder().name("Home & Living").description("Furniture, Decor, Kitchenware, Appliances").active(true).build(),
                Category.builder().name("Beauty & Personal Care").description("Skincare, Grooming, Cosmetics, Health").active(true).build(),
                Category.builder().name("Sports & Fitness").description("Activewear, Equipment, Outdoor Gear").active(true).build(),
                Category.builder().name("Books & Media").description("Literature, E-books, Educational Material").active(true).build(),
                Category.builder().name("Toys & Games").description("Games, Puzzles, Collectibles, Hobbies").active(true).build()
            );

            categoryRepository.saveAll(defaultCategories);
            log.info("Successfully initialized {} default categories.", defaultCategories.size());
        }

        // 2. Initialize default Admin user if empty
        if (!userRepository.existsByEmail("admin@shoppersclub.com")) {
            log.info("No admin user found. Creating default platform administrator...");

            User adminUser = User.builder()
                .firstName("Platform")
                .lastName("Admin")
                .email("admin@shoppersclub.com")
                .password(passwordEncoder.encode("Admin@123456"))
                .roles(Set.of(Role.ADMIN, Role.SELLER, Role.CUSTOMER))
                .active(true)
                .build();

            userRepository.save(adminUser);
            log.info("Successfully created default Admin account [admin@shoppersclub.com].");
        }
    }
}
