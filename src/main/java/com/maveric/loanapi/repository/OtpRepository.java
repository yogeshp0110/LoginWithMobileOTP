package com.maveric.loanapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maveric.loanapi.model.OtpEntity;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findByPhoneNumber(String phoneNumber);

    Optional<OtpEntity> findByPhoneNumberAndOtp(String phoneNumber, String otp);
}
