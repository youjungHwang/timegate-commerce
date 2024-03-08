package com.example.orders_service.common.handler.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //4xx
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_AVAILABLE_TIME(HttpStatus.BAD_REQUEST, "구매 가능 시간이 아닙니다"),
    PRICE_MISMATCH(HttpStatus.BAD_REQUEST, "요청된 가격과 상품의 실제 가격이 일치하지 않습니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 상품이 존재하지 않습니다"),
    STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "요청한 수량이 재고보다 많습니다. 사용 가능한 재고 수량을 확인하고 다시 시도해주세요."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),
    ORDER_PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제 실패한 주문입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태 변경입니다."),

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
