package com.fitnesscenter.payment;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

  @Value("${stripe.secret.key}")
  private String secretKey;

  @PostConstruct
  public void init() {
    if (secretKey == null || secretKey.isBlank()) {
      throw new IllegalStateException("stripe.secret.key is missing/blank");
    }
    System.out.println("STRIPE KEY LOADED = " + secretKey.substring(0, 12) + "...");
    Stripe.apiKey = secretKey;
  }
}
