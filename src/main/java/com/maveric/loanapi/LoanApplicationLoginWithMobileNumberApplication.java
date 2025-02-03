package com.maveric.loanapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.maveric.loanapi.config.TwilioConfig;
import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class LoanApplicationLoginWithMobileNumberApplication {
	
	 @Autowired
	    private TwilioConfig twilioConfig;

	    @PostConstruct
	    public void initTwilio() {
	        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	    }

	public static void main(String[] args) {
		SpringApplication.run(LoanApplicationLoginWithMobileNumberApplication.class, args);
	}

}
