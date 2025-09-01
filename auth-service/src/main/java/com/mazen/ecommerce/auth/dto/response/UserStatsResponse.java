package com.mazen.ecommerce.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsResponse {

    private Long totalUsers;
    private Long activeUsers;
    private Long adminUsers;
    private Long sellerUsers;
    private Long regularUsers;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;
}

