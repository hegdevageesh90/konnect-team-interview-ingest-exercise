package com.kong.konnect.search.service;

import com.kong.konnect.search.config.AppConfigProperties;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CDCEventProcessor {

  private static final Logger logger = LoggerFactory.getLogger(CDCEventProcessor.class);
  private final KafkaProducerService kafkaProducerService;
  private final AppConfigProperties appConfigProperties;

  public CDCEventProcessor(
      KafkaProducerService kafkaProducerService, AppConfigProperties appConfigProperties) {
    this.kafkaProducerService = kafkaProducerService;
    this.appConfigProperties = appConfigProperties;
  }

  @PostConstruct
  public void processEvents() {
    File file = new File(appConfigProperties.getCdc().getFilePath());

    if (!file.exists()) {
      logger.error("JSONL file not found at {}", appConfigProperties.getCdc().getFilePath());
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        try {
          kafkaProducerService.sendMessage(line);
          logger.info("Processed and sent CDC event to Kafka: {}", line);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    } catch (IOException e) {
      logger.error("Error reading JSONL file", e);
    }
  }
}
