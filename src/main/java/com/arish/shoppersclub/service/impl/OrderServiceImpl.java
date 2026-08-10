package com.arish.shoppersclub.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateOrderRequest;
import com.arish.shoppersclub.dto.response.OrderItemResponse;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.entity.Address;
import com.arish.shoppersclub.entity.Cart;
import com.arish.shoppersclub.entity.CartItem;
import com.arish.shoppersclub.entity.Order;
import com.arish.shoppersclub.entity.OrderItem;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.enums.PaymentStatus;
import com.arish.shoppersclub.enums.ProductStatus;
import com.arish.shoppersclub.exception.EmptyCartException;
import com.arish.shoppersclub.exception.InsufficientStockException;
import com.arish.shoppersclub.exception.OrderCancellationNotAllowedException;
import com.arish.shoppersclub.exception.OrderNotFoundException;
import com.arish.shoppersclub.exception.ProductUnavailableException;
import com.arish.shoppersclub.mapper.OrderMapper;
import com.arish.shoppersclub.repository.AddressRepository;
import com.arish.shoppersclub.repository.CartItemRepository;
import com.arish.shoppersclub.repository.CartRepository;
import com.arish.shoppersclub.repository.OrderItemRepository;
import com.arish.shoppersclub.repository.OrderRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.arish.shoppersclub.service.EmailService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest request) {
        User user = getAuthenticatedUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EmptyCartException("Cart does not exist for the authenticated user"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot place an order with an empty cart");
        }

        Address address = addressRepository.findByIdAndUser(request.addressId(), user)
                .orElseThrow(() -> new RuntimeException("Shipping address not found with id: " + request.addressId()));

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ProductUnavailableException("Product '" + product.getName() + "' is not active or available for purchase");
            }
            if (cartItem.getQuantity() > product.getStock()) {
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity());
            }
        }

        Order order = Order.builder()
                .user(user)
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();

        Order savedOrder = orderRepository.save(order);

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;
        int calculatedTotalItems = 0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            product.setStock(product.getStock() - quantity);
            if (product.getStock() == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            productRepository.save(product);

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .productName(product.getName())
                    .price(unitPrice)
                    .quantity(quantity)
                    .subtotal(subtotal)
                    .build();

            orderItemRepository.save(orderItem);

            calculatedTotalAmount = calculatedTotalAmount.add(subtotal);
            calculatedTotalItems += quantity;
        }

        savedOrder.setTotalAmount(calculatedTotalAmount);
        savedOrder.setTotalItems(calculatedTotalItems);
        savedOrder = orderRepository.save(savedOrder);

        cartItemRepository.deleteByCart(cart);

        // Send order placement confirmation email
        emailService.sendOrderPlacedNotification(
            user.getEmail(),
            savedOrder.getId(),
            savedOrder.getTotalAmount(),
            savedOrder.getTotalItems()
        );

        return buildOrderResponse(savedOrder);
    }

    @Override
    public PagedResponse<OrderResponse> getMyOrders(int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = getAuthenticatedUser();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);

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

    @Override
    public OrderResponse getOrderById(Long id) {
        User user = getAuthenticatedUser();
        Order order = orderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        User user = getAuthenticatedUser();
        Order order = orderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new OrderCancellationNotAllowedException("Order cannot be cancelled because its current status is " + order.getOrderStatus());
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());

            if (product.getStock() > 0 && product.getStatus() == ProductStatus.OUT_OF_STOCK) {
                product.setStatus(ProductStatus.ACTIVE);
            }

            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return buildOrderResponse(updatedOrder);
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

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
