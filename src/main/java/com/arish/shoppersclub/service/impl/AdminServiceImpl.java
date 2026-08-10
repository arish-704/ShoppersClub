package com.arish.shoppersclub.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.AdminProductStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateOrderStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerVerificationRequest;
import com.arish.shoppersclub.dto.response.AdminPlatformOverviewResponse;
import com.arish.shoppersclub.dto.response.OrderItemResponse;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.dto.response.SellerResponse;
import com.arish.shoppersclub.entity.Order;
import com.arish.shoppersclub.entity.OrderItem;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.exception.OrderNotFoundException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.SellerNotFoundException;
import com.arish.shoppersclub.mapper.OrderMapper;
import com.arish.shoppersclub.mapper.ProductMapper;
import com.arish.shoppersclub.mapper.SellerMapper;
import com.arish.shoppersclub.repository.OrderItemRepository;
import com.arish.shoppersclub.repository.OrderRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.SellerRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.AdminService;

import lombok.RequiredArgsConstructor;

import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.service.EmailService;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SellerRepository sellerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final com.arish.shoppersclub.mapper.UserMapper userMapper;
    private final EmailService emailService;

    @Override
    public SellerResponse verifySeller(Long sellerId, UpdateSellerVerificationRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller store not found with id: " + sellerId));

        seller.setVerified(request.verified());
        Seller updatedSeller = sellerRepository.save(seller);
        return sellerMapper.toResponse(updatedSeller);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        OrderStatus previousStatus = order.getOrderStatus();
        order.setOrderStatus(request.orderStatus());
        if (request.paymentStatus() != null) {
            order.setPaymentStatus(request.paymentStatus());
        }

        Order updatedOrder = orderRepository.save(order);

        // Send order shipped email notification when status changes to SHIPPED
        if (request.orderStatus() == OrderStatus.SHIPPED && previousStatus != OrderStatus.SHIPPED) {
            emailService.sendOrderShippedNotification(
                order.getUser().getEmail(),
                order.getId()
            );
        }

        return buildOrderResponse(updatedOrder);
    }

    @Override
    public ProductResponse updateProductStatus(Long productId, AdminProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        product.setStatus(request.status());
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(productMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    public PagedResponse<OrderResponse> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .toList();

        return new PagedResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    /**
     * Retrieves a paginated list of all seller stores.
     * Optionally filters by verification status (verified=true / verified=false).
     */
    @Override
    public PagedResponse<SellerResponse> getAllSellers(Boolean verified, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Seller> sellerPage = (verified != null)
                ? sellerRepository.findByVerified(verified, pageable)
                : sellerRepository.findAll(pageable);

        List<SellerResponse> content = sellerPage.getContent().stream()
                .map(sellerMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                sellerPage.getNumber(),
                sellerPage.getSize(),
                sellerPage.getTotalElements(),
                sellerPage.getTotalPages(),
                sellerPage.isLast()
        );
    }

    @Override
    public PagedResponse<com.arish.shoppersclub.dto.response.UserProfileResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<com.arish.shoppersclub.entity.User> userPage = userRepository.findAll(pageable);

        List<com.arish.shoppersclub.dto.response.UserProfileResponse> content = userPage.getContent().stream()
                .map(userMapper::toProfileResponse)
                .toList();

        return new PagedResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    public AdminPlatformOverviewResponse getPlatformOverview() {
        long totalUsers = userRepository.count();
        long totalSellers = sellerRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return new AdminPlatformOverviewResponse(
                totalUsers,
                totalSellers,
                totalProducts,
                totalOrders,
                totalRevenue
        );
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(orderMapper::toOrderItemResponse)
                .toList();
        return orderMapper.toOrderResponse(order, itemResponses);
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pageNo, pageSize, sort);
    }
}
