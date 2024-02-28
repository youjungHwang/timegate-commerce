package com.example.product_service.common.dto.response;

import org.springframework.http.HttpStatus;
public record ApiResponse<T>(HttpStatus status, String message, T data) {}