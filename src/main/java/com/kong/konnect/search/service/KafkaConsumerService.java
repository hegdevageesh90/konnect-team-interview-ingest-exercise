package com.kong.konnect.search.service;

import com.google.gson.Gson;
import com.kong.konnect.search.config.AppConfigProperties;
import com.kong.konnect.search.model.CDCEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consume from Kafka and send for indexing to OpenSearch
 *
 * @author hegdevageesh
 */
@Service
public class KafkaConsumerService {
  private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
  private final OpenSearchService openSearchService;
  private final AppConfigProperties appConfigProperties;
  private final Gson gson = new Gson();

  public KafkaConsumerService(
      OpenSearchService openSearchService, AppConfigProperties appConfigProperties) {
    this.openSearchService = openSearchService;
    this.appConfigProperties = appConfigProperties;
  }

  /**
   * Consumes from Kafka, picks the value, and sends to be indexed to OpenSearch. Method also does
   * an object conversion to verify sanity.
   *
   * @param record that is consumed
   */
  @KafkaListener(
      topics = "#{appConfigProperties.kafka.topicName}",
      groupId = "#{appConfigProperties.kafka.consumerGroupId}")
  public void consume(ConsumerRecord<String, String> record) {
    logger.info("Consuming message: {}", record.value());
    try {
      CDCEvent event = gson.fromJson(record.value(), CDCEvent.class);
      openSearchService.indexEvent(gson.toJson(event));
    } catch (Exception e) {
      logger.error("Error processing message", e);
    }
  }
}
