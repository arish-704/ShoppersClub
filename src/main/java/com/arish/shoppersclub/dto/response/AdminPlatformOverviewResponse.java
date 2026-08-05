package com.arish.shoppersclub.dto.response;

import java.math.BigDecimal;

public record AdminPlatformOverviewResponse(
    long totalUsers,
    long totalSellers,
    long totalProducts,
    long totalOrders,
    BigDecimal totalRevenue
) {

}
