package com.kong.konnect.search.service;

import static org.mockito.Mockito.*;

import com.kong.konnect.search.config.properties.CdcProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CDCEventProcessorTest {

  @Mock private KafkaProducerService kafkaProducerService;

  @Mock private CdcProperties cdcProperties;

  @InjectMocks private CDCEventProcessor cdcEventProcessor;

  private static final String VALID_FILE_PATH = "src/test/resources/valid.jsonl";
  private static final String INVALID_FILE_PATH = "src/test/resources/invalidfile.jsonl";
  private static final String EMPTY_FILE_PATH = "src/test/resources/empty.jsonl";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testProcessEventsWithValidFile() {
    setupConfigAndFilePath(VALID_FILE_PATH);

    cdcEventProcessor.processEvents();

    verify(kafkaProducerService, times(1)).sendMessage("{\"key1\": \"value1\"}");
    verify(kafkaProducerService, times(1)).sendMessage("{\"key2\": \"value2\"}");
  }

  @Test
  void testProcessEventsWithInvalidFile() {
    setupConfigAndFilePath(INVALID_FILE_PATH);

    cdcEventProcessor.processEvents();

    verify(kafkaProducerService, never()).sendMessage(anyString());
  }

  @Test
  void testProcessEventsWithKafkaError() {
    setupConfigAndFilePath(VALID_FILE_PATH);
    doThrow(new RuntimeException("Kafka failure"))
        .when(kafkaProducerService)
        .sendMessage("{\"key1\": \"value1\"}");

    try {
      cdcEventProcessor.processEvents();
    } catch (RuntimeException e) {
      verify(kafkaProducerService, times(1)).sendMessage("{\"key1\": \"value1\"}");
      verify(kafkaProducerService, never()).sendMessage("{\"key2\": \"value2\"}");
    }
  }

  @Test
  void testProcessEventsWithEmptyFile() {
    setupConfigAndFilePath(EMPTY_FILE_PATH);

    cdcEventProcessor.processEvents();

    verify(kafkaProducerService, never()).sendMessage(anyString());
  }

  private void setupConfigAndFilePath(String filePath) {
    when(cdcProperties.getFilePath()).thenReturn(filePath);
  }
}
