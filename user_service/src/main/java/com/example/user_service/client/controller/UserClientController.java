package com.example.user_service.client.controller;

import com.example.user_service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user-service")
@RestController
public class UserClientController {
    private final UserService userService;

}
