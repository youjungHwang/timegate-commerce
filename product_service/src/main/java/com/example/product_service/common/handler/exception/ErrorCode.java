package com.example.product_service.common.handler.exception;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 4xx
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "상품에 대한 재고정보를 찾을 수 없습니다."),
    STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "요청된 수량이 재고보다 많습니다."),
    DELETED_ITEM(HttpStatus.BAD_REQUEST, "이미 삭제된 항목입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 있습니다."),

    // Feign 클라이언트 통신 실패 관련 에러 코드
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "외부 서비스 호출에 실패했습니다."),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "외부 서비스 응답 시간 초과입니다."),
    EXTERNAL_SERVICE_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "외부 서비스에서 클라이언트 오류가 발생했습니다."),
    EXTERNAL_SERVICE_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "외부 서비스에서 서버 오류가 발생했습니다.");

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
