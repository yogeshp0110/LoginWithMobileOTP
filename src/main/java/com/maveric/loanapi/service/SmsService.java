package com.maveric.loanapi.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maveric.loanapi.config.TwilioConfig;
import com.maveric.loanapi.dto.OtpRequestDto;
import com.maveric.loanapi.dto.OtpResponseDto;
import com.maveric.loanapi.dto.OtpStatus;
import com.maveric.loanapi.dto.OtpValidationRequestDto;
import com.maveric.loanapi.dto.OtpValidationResponseDto;
import com.maveric.loanapi.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Map<String, String> otpMap = new HashMap<>();
    private static final Map<String, Long> otpTimestampMap = new HashMap<>();  // Store timestamp of OTP generation

    private static final int OTP_EXPIRY_TIME_IN_MINUTES = 5; // OTP expires after 5 minutes

    @Autowired
    private TwilioConfig twilioConfig;

    // Method to send OTP via SMS
    public OtpResponseDto sendSMS(OtpRequestDto otpRequest) {
        log.info("Attempting to send OTP to phone number: {}", otpRequest.getPhoneNumber());

        // Validate mobile number (must be 10 digits)
        if (!isValidMobileNumber(otpRequest.getPhoneNumber())) {
            log.warn("Invalid mobile number: {}", otpRequest.getPhoneNumber());
            return new OtpResponseDto(OtpStatus.FAILED, "Invalid mobile number. It should be 10 digits.");
        }

        OtpResponseDto otpResponseDto = null;
        try {
            // Generate OTP
            String otp = generateOTP();
            String otpMessage = "Dear Customer, Your OTP for SMS verification is: " + otp + ". Thank you.";
            log.debug("Generated OTP: {}", otp);

            // Store OTP and timestamp for validation
            otpMap.put(otpRequest.getPhoneNumber(), otp);
            otpTimestampMap.put(otpRequest.getPhoneNumber(), System.currentTimeMillis());  // Store current timestamp
            log.info("OTP stored for phone number: {}", otpRequest.getPhoneNumber());

            otpResponseDto = new OtpResponseDto(OtpStatus.DELIVERED, otpMessage);
            log.info("OTP sent successfully to: {}", otpRequest.getPhoneNumber());

        } catch (Exception e) {
            log.error("Failed to send OTP to: {}", otpRequest.getPhoneNumber(), e);
            otpResponseDto = new OtpResponseDto(OtpStatus.FAILED, "Error sending OTP: " + e.getMessage());
        }

        return otpResponseDto;
    }

    // Updated validateOtp method to return a structured response instead of a String
    public OtpValidationResponseDto validateOtp(OtpValidationRequestDto otpValidationRequest) {
        log.info("Validating OTP for phone number: {}", otpValidationRequest.getPhoneNumber());

        OtpValidationResponseDto response = new OtpValidationResponseDto();

        try {
            // Get OTP and timestamp stored for the phone number
            String storedOtp = otpMap.get(otpValidationRequest.getPhoneNumber());
            Long otpTimestamp = otpTimestampMap.get(otpValidationRequest.getPhoneNumber());

            log.debug("Stored OTP: {}", storedOtp);
            log.debug("OTP timestamp: {}", otpTimestamp);

            // Check if OTP has expired (greater than 5 minutes)
            if (otpTimestamp != null && System.currentTimeMillis() - otpTimestamp > TimeUnit.MINUTES.toMillis(OTP_EXPIRY_TIME_IN_MINUTES)) {
                otpMap.remove(otpValidationRequest.getPhoneNumber());  // Remove expired OTP
                otpTimestampMap.remove(otpValidationRequest.getPhoneNumber());  // Remove expired timestamp
                log.warn("OTP expired for phone number: {}", otpValidationRequest.getPhoneNumber());
                response.setMessage("OTP has expired!");
                return response;
            }

            // Validate the OTP
            if (storedOtp != null && storedOtp.equals(otpValidationRequest.getOtpNumber())) {
                otpMap.remove(otpValidationRequest.getPhoneNumber());  // Remove OTP after successful validation
                otpTimestampMap.remove(otpValidationRequest.getPhoneNumber());  // Remove timestamp

                log.info("OTP validated successfully for phone number: {}", otpValidationRequest.getPhoneNumber());

                // If OTP is valid, generate and return JWT token
                String jwtToken = jwtUtil.generateToken(otpValidationRequest.getPhoneNumber());
                response.setMessage("OTP is valid!");
                response.setJwtToken(jwtToken);
                log.info("JWT Token generated for phone number: {}", otpValidationRequest.getPhoneNumber());
            } else {
                response.setMessage("OTP is invalid or expired!");
                log.warn("Invalid OTP entered for phone number: {}", otpValidationRequest.getPhoneNumber());
            }
        } catch (Exception e) {
            log.error("Error validating OTP for phone number: {}", otpValidationRequest.getPhoneNumber(), e);
            response.setMessage("Error during OTP validation: " + e.getMessage());
        }

        return response;
    }

    // Helper method to generate a 6-digit OTP
    private String generateOTP() {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        log.debug("Generated OTP: {}", otp);
        return otp;
    }

    // Helper method to validate mobile number (10 digits)
    private boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber != null && mobileNumber.matches("\\d{10}");
    }
}
