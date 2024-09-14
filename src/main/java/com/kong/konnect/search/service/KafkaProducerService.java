package com.kong.konnect.search.service;

import com.kong.konnect.search.config.AppConfigProperties;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final AppConfigProperties appConfigProperties;

  public KafkaProducerService(
      KafkaTemplate<String, String> kafkaTemplate, AppConfigProperties appConfigProperties) {
    this.kafkaTemplate = kafkaTemplate;
    this.appConfigProperties = appConfigProperties;
  }

  public void sendMessage(String message) {
    logger.info(
        "Sending message to Kafka topic {}: {}",
        appConfigProperties.getKafka().getTopicName(),
        message);

    CompletableFuture<SendResult<String, String>> future =
        kafkaTemplate.send(appConfigProperties.getKafka().getTopicName(), message);

    future
        .thenAccept(
            result ->
                logger.info(
                    "Message sent successfully to topic {} with offset {}",
                    appConfigProperties.getKafka().getTopicName(),
                    result.getRecordMetadata().offset()))
        .exceptionally(
            ex -> {
              logger.error(
                  "Failed to send message to topic {}",
                  appConfigProperties.getKafka().getTopicName(),
                  ex);
              return null;
            });
  }
}
