package com.kong.konnect.search.service;

import com.kong.konnect.search.config.AppConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Produce an event to configured topic
 *
 * @author hegdevageesh
 */
@Service
public class KafkaProducerService {

  private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicName;

  public KafkaProducerService(
      KafkaTemplate<String, String> kafkaTemplate, AppConfigProperties appConfigProperties) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicName = appConfigProperties.getKafka().getTopicName(); // Cached for reuse
  }

  /**
   * Sends a message with appropriate logs for success/failure. Note that failures are error logged
   * and not breaking.
   *
   * @param message event to be produced
   */
  public void sendMessage(String message) {
    kafkaTemplate
        .send(topicName, message)
        .thenAccept(
            result ->
                logger.info(
                    "Message sent successfully with offset {}",
                    result.getRecordMetadata().offset()))
        .exceptionally(
            ex -> {
              logger.error("Failed to send message to topic {}", topicName, ex);
              return null;
            });
  }
}
