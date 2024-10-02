package com.kong.konnect.search.service;

import static org.mockito.Mockito.*;

import com.google.gson.JsonSyntaxException;
import com.kong.konnect.search.config.properties.KafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {KafkaConsumerService.class})
class KafkaConsumerServiceTest {
  @InjectMocks private KafkaConsumerService kafkaConsumerService;

  @Mock private KafkaProperties kafkaProperties;

  @Mock private OpenSearchService openSearchService;

  private static final String VALID_CDC_EVENT =
      "{\n"
          + "  \"before\": {},\n"
          + "  \"after\": {\n"
          + "    \"key\": \"someKey\",\n"
          + "    \"value\": {\n"
          + "      \"type\": 1,\n"
          + "      \"object\": \"someValue\"\n"
          + "    }\n"
          + "  },\n"
          + "  \"op\": \"INSERT\",\n"
          + "  \"ts_ms\": 1695736484000\n"
          + "}";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testConsumeAnyEvent() throws JsonSyntaxException {
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn("foo");

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
  }

  @Test
  void testConsumeNull() {
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn(null);

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, times(2)).value();
    verify(openSearchService, times(0)).indexEvent(any());
  }

  @Test
  void testConsumeEmpty() {
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn("");

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
    verify(openSearchService, times(0)).indexEvent(any());
  }

  @Test
  void testConsumeValidEvent() {
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn(VALID_CDC_EVENT);

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
    verify(openSearchService, times(1)).indexEvent(any());
  }
}
