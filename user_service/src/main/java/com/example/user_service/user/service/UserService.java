package com.example.user_service.user.service;

import com.example.user_service.common.handler.exception.CustomException;
import com.example.user_service.common.handler.exception.ErrorCode;
import com.example.user_service.user.dto.request.SignUpRequestDto;
import com.example.user_service.user.dto.response.SignUpResponseDto;
import com.example.user_service.user.entity.User;
import com.example.user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        if (signUpRequestDto.email() == null || signUpRequestDto.email().isBlank()) {
            throw new CustomException(ErrorCode.EMAIL_REQUIRED);
        }

        boolean existsByEmail = userRepository.existsByEmail(signUpRequestDto.email());
        if (existsByEmail) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }

        User user = User.builder()
                .email(signUpRequestDto.email())
                .build();
        User savedUser = userRepository.save(user);

        return new SignUpResponseDto(savedUser.getId(), savedUser.getEmail());
    }

}
