package com.kong.konnect.search.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class CDCEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CDCEventProcessor.class);
    private static final String FILE_PATH = "stream.jsonl";

    private final KafkaProducerService kafkaProducerService;

    public CDCEventProcessor(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostConstruct
    public void processEvents() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            logger.error("JSONL file not found at {}", FILE_PATH);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    kafkaProducerService.sendMessage("konnect-cdc-events", line);
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
