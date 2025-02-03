package com.maveric.loanapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidationRequestDto {
    private String phoneNumber;
    private String otpNumber;
}
