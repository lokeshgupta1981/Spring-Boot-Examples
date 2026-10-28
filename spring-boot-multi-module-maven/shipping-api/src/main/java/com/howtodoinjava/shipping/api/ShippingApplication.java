package com.howtodoinjava.shipping.api;
import com.howtodoinjava.shipping.core.ShippingQuoteCalculator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class ShippingApplication {
  public static void main(String[] args) { SpringApplication.run(ShippingApplication.class, args); }
  @Bean ShippingQuoteCalculator shippingQuoteCalculator() { return new ShippingQuoteCalculator(); }
}
