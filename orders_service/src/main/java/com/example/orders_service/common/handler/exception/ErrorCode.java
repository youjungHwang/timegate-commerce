package com.example.orders_service.common.handler.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //4xx
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    NOT_AVAILABLE_TIME(HttpStatus.BAD_REQUEST, "구매 가능 시간이 아닙니다"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 상품이 존재하지 않습니다"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "주문하려는 상품의 재고가 없습니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 있습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
