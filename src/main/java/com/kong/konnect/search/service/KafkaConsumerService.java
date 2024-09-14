package com.kong.konnect.search.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kong.konnect.search.model.CDCEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final OpenSearchService openSearchService;

    public KafkaConsumerService(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
    }

    @KafkaListener(topics = "konnect-cdc-events", groupId = "konnect-search-group")
    public void consume(ConsumerRecord<String, String> record) {
        logger.info("Consuming message: {}", record.value());
        try {
            Gson gson = new GsonBuilder().create();
            String jsonString = record.value();
            logger.debug("Raw JSON Message: {}", jsonString);

            String cleanedJson = jsonString
                    .replaceFirst("^\"", "") // Remove leading quote
                    .replaceFirst("\"$", "") // Remove trailing quote
                    .replace("\\\"", "\"");  // Replace escaped quotes with normal quotes

            CDCEvent event = gson.fromJson(cleanedJson, CDCEvent.class);
            String indexedJson = gson.toJson(event);
            openSearchService.indexEvent(indexedJson);
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
}
