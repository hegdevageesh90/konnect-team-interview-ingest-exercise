package com.kong.konnect.search.service;

import com.kong.konnect.search.config.properties.CdcProperties;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * CDC JSON processor
 *
 * @author hegdevageesh
 */
@Service
public class CDCEventProcessor {

  private static final Logger logger = LoggerFactory.getLogger(CDCEventProcessor.class);
  private final KafkaProducerService kafkaProducerService;
  private final CdcProperties cdcProperties;

  public CDCEventProcessor(KafkaProducerService kafkaProducerService, CdcProperties cdcProperties) {
    this.kafkaProducerService = kafkaProducerService;
    this.cdcProperties = cdcProperties;
  }

  /** Reads CDC JSONs from stream data file line by line and sends to be produced to Kafka */
  @PostConstruct
  public void processEvents() {
    var file = new File(cdcProperties.getFilePath());

    Optional.of(file)
        .filter(File::exists)
        .ifPresentOrElse(
            this::processFile,
            () -> logger.error("JSONL file not found at {}", cdcProperties.getFilePath()));
  }

  private void processFile(File file) {
    try (var reader = new BufferedReader(new FileReader(file))) {
      reader
          .lines()
          .forEach(
              line -> {
                try {
                  kafkaProducerService.sendMessage(line);
                  logger.debug("Processed and sent CDC event to Kafka: {}", line);
                } catch (Exception e) {
                  throw new RuntimeException("Failed to process CDC event", e);
                }
              });
    } catch (IOException e) {
      logger.error("Error reading JSONL file at {}", cdcProperties.getFilePath(), e);
    }
  }
}
