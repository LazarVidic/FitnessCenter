package com.fitnesscenter.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(PasswordEncoder pe) {
        return args -> {
        	   System.out.println("USER stefanStevanovic = " + pe.encode("stefanStevanovic"));
        	    System.out.println("USER LazarIlicc      = " + pe.encode("LazarIlicc"));
        	    System.out.println("SELLER Milos123L     = " + pe.encode("Milos123L"));
        	    System.out.println("ADMIN Uros123L       = " + pe.encode("Uros123L"));
        };
    }
}
