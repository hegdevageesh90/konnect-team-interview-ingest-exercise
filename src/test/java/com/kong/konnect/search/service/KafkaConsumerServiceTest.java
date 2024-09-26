package com.kong.konnect.search.service;

import static org.mockito.Mockito.*;

import com.google.gson.JsonSyntaxException;
import com.kong.konnect.search.config.AppConfigProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {KafkaConsumerService.class})
class KafkaConsumerServiceTest {
  @Autowired private KafkaConsumerService kafkaConsumerService;

  @MockBean private AppConfigProperties.KafkaProperties kafkaProperties;

  @MockBean private OpenSearchService openSearchService;

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

  @Test
  void testConsumeAnyEvent() throws JsonSyntaxException {
    OpenSearchService openSearchService =
        new OpenSearchService(null, new AppConfigProperties.OpenSearchProperties());
    KafkaConsumerService kafkaConsumerService =
        new KafkaConsumerService(openSearchService, new AppConfigProperties.KafkaProperties());
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn("foo");

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
  }

  @Test
  void testConsumeNull() {
    OpenSearchService openSearchService = mock(OpenSearchService.class);
    doNothing().when(openSearchService).indexEvent(Mockito.any());
    KafkaConsumerService kafkaConsumerService =
        new KafkaConsumerService(openSearchService, new AppConfigProperties.KafkaProperties());
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn(null);

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, times(2)).value();
    verify(openSearchService, times(0)).indexEvent(any());
  }

  @Test
  void testConsumeEmpty() {
    OpenSearchService openSearchService = mock(OpenSearchService.class);
    doNothing().when(openSearchService).indexEvent(Mockito.any());
    KafkaConsumerService kafkaConsumerService =
        new KafkaConsumerService(null, new AppConfigProperties.KafkaProperties());
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn("");

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
    verify(openSearchService, times(0)).indexEvent(any());
  }

  @Test
  void testConsumeValidEvent() {
    OpenSearchService openSearchService = mock(OpenSearchService.class);
    doNothing().when(openSearchService).indexEvent(Mockito.any());
    KafkaConsumerService kafkaConsumerService =
        new KafkaConsumerService(openSearchService, new AppConfigProperties.KafkaProperties());
    ConsumerRecord<String, String> resultRecord = mock(ConsumerRecord.class);
    when(resultRecord.value()).thenReturn(VALID_CDC_EVENT);

    kafkaConsumerService.consume(resultRecord);

    verify(resultRecord, atLeast(1)).value();
    verify(openSearchService, times(1)).indexEvent(any());
  }
}
