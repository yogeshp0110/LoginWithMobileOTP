package com.maveric.loanapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maveric.loanapi.dto.OtpRequestDto;
import com.maveric.loanapi.dto.OtpResponseDto;
import com.maveric.loanapi.service.SmsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/otp")
@Slf4j
public class OtpController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public OtpResponseDto sendOtp(@RequestBody OtpRequestDto otpRequestDto) {
        log.info("Received request to send OTP to phone number: {}", otpRequestDto.getPhoneNumber());

        OtpResponseDto response = smsService.sendSMS(otpRequestDto);

        if (response.getStatus() == null || response.getStatus().equals("FAILED")) {
            log.error("Failed to send OTP to phone number: {}", otpRequestDto.getPhoneNumber());
        } else {
            log.info("OTP sent successfully to phone number: {}", otpRequestDto.getPhoneNumber());
            log.debug("OTP message: {}", response.getMessage());
        }

        return response;
    }
}
