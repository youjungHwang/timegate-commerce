package com.example.payment_service.client.orders.enums;

public enum OrderType {
    INITIATED, // 주문 시작 (고객이 주문을 생성했지만, 아직 결제 과정을 진행하지 않은 상태)
    IN_PROGRESS, // 주문 처리가 진행 중 (결제 진행 중)
    COMPLETED, // 주문 완료
    FAILED_CUSTOMER, // 고객 귀책 사유로 인한 실패
    CANCEL // 주문 취소
}
