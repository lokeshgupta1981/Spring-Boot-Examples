package com.howtodoinjava.demo.batch.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.howtodoinjava.demo.batch.quartz"})
public class BatchJobByQuartzApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BatchJobByQuartzApplication.class);
    app.setAdditionalProfiles("quartz");
    app.run(args);
  }
}