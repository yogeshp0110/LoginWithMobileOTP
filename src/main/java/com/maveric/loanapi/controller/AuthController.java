package com.maveric.loanapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maveric.loanapi.dto.AuthResponseDto;
import com.maveric.loanapi.dto.OtpValidationRequestDto;
import com.maveric.loanapi.dto.OtpValidationResponseDto;
import com.maveric.loanapi.service.SmsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final SmsService smsService;

    public AuthController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody OtpValidationRequestDto otpValidationRequest) {
        log.info("Received login request for phone number: {}", otpValidationRequest.getPhoneNumber());

        OtpValidationResponseDto validationResponse = smsService.validateOtp(otpValidationRequest);

        AuthResponseDto response = new AuthResponseDto();
        response.setMessage(validationResponse.getMessage());
        response.setJwtToken(validationResponse.getJwtToken());

        if ("OTP is valid!".equals(validationResponse.getMessage())) {
            log.info("OTP validated successfully for phone number: {}", otpValidationRequest.getPhoneNumber());
            log.debug("JWT Token generated: {}", validationResponse.getJwtToken());
        } else {
            log.warn("OTP validation failed for phone number: {}", otpValidationRequest.getPhoneNumber());
        }

        return response;
    }
}
