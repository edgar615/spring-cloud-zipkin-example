package com.github.edgar615.spring.consul.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConsulStudentApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudConsulStudentApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(SpringCloudConsulStudentApplication.class, args);
  }

  @KafkaListener(topics = "myTopic")
  public void processMessage9(String content) {
    LOGGER.info("received message: {}", content);
  }
}
