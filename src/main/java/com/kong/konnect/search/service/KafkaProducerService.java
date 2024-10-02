package com.kong.konnect.search.service;

import com.kong.konnect.search.config.properties.KafkaProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
      KafkaTemplate<String, String> kafkaTemplate, KafkaProperties kafkaProperties) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicName = kafkaProperties.getTopicName();
  }

  /**
   * Sends a message with appropriate logs for success/failure. Note that failures are error logged
   * and not breaking.
   *
   * @param message event to be produced
   */
  @CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "fallback")
  @Retry(name = "kafkaRetry")
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

  /**
   * Fallback method for circuit breaker failure.
   *
   * @param message the message that failed
   * @param ex the exception that triggered the fallback
   */
  public void fallback(String message, Throwable ex) {
    logger.error(
        "Kafka Circuit breaker activated. Fallback triggered for message: {}", message, ex);
    // TODO : add fallback impl. example : index to a backup instance/cluster.
  }
}
