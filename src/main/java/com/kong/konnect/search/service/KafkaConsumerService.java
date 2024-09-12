package com.kong.konnect.search.service;

import com.kong.konnect.search.model.CDCEvent;
import com.kong.konnect.search.util.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final OpenSearchService openSearchService;
    private final JsonParser jsonParser;

    public KafkaConsumerService(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
        this.jsonParser = new JsonParser();
    }

    @KafkaListener(topics = "konnect-cdc-events", groupId = "konnect-search-group")
    public void consume(ConsumerRecord<String, String> record) {
        logger.info("Consuming message: {}", record.value());
        try {
            CDCEvent event = jsonParser.fromJson(record.value(), CDCEvent.class);
            openSearchService.indexEvent(event);
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
}
