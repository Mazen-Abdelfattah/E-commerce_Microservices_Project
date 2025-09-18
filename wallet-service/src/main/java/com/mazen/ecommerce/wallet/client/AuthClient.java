package com.mazen.ecommerce.wallet.client;

import com.mazen.ecommerce.wallet.client.dto.ApiResponse;
import com.mazen.ecommerce.wallet.client.dto.TokenValidationResponse;
import com.mazen.ecommerce.wallet.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @GetMapping("/api/internal/validate-token")
    ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(@RequestHeader("Authorization") String authHeader);


    @GetMapping("/api/internal/users/{userId}")
    ResponseEntity<ApiResponse<UserResponse>> getUserByIdInternal(@PathVariable Long userId);



}

