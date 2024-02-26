package com.example.payment_service.common.handler.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보가 존재하지 않습니다."),
    ORDER_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "주문이 진행 중인 상태가 아닙니다."),
    DELETED_PAYMENT(HttpStatus.NOT_FOUND, "이미 취소된 주문입니다."),
    FAILED_CUSTOMER(HttpStatus.BAD_REQUEST, "사용자 귀책 사유로 결제가 실패했습니다."),
    PAYMENT_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 과정에 실패했습니다."),
    STOCK_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "재고 업데이트에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 내역이 존재하지 않습니다."),
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