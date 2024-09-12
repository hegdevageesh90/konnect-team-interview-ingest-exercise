package com.kong.konnect.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        logger.info("Sending message to Kafka topic {}: {}", topic, message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.thenAccept(result -> logger.info("Message sent successfully to topic {} with offset {}", topic, result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    logger.error("Failed to send message to topic {}", topic, ex);
                    return null;
                });
    }
}
