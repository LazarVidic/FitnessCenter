package com.fitnesscenter.fitnesscenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.fitnesscenter") 
@EntityScan(basePackages = "com.fitnesscenter")
@EnableJpaRepositories(basePackages = "com.fitnesscenter")
public class FitnesscenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(FitnesscenterApplication.class, args);
  }
}
