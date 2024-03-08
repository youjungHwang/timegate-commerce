package com.example.payment_service.common.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomDecisionMaker {

    public boolean shouldFailPayment() {
        return new Random().nextInt(100) < 20;
    }
}
