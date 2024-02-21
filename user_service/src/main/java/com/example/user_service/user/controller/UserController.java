package com.example.user_service.user.controller;


import com.example.user_service.common.dto.response.ApiResponse;
import com.example.user_service.user.dto.request.SignUpRequestDto;
import com.example.user_service.user.dto.response.SignUpResponseDto;
import com.example.user_service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user-service")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponseDto = userService.signUp(signUpRequestDto);
        ApiResponse<SignUpResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "이메일로 회원가입 성공",
                signUpResponseDto
        );
        return ResponseEntity.ok(response);
    }

}

