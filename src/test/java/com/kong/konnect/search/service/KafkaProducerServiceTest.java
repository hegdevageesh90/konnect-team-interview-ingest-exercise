package com.kong.konnect.search.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kong.konnect.search.config.properties.KafkaProperties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.kafka.common.internals.KafkaCompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {KafkaProducerService.class})
class KafkaProducerServiceTest {
  private static final String message = "Not all who wander are lost";

  @Test
  void testSendMessage() {
    KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    when(kafkaTemplate.send(Mockito.any(), Mockito.any())).thenReturn(new CompletableFuture<>());

    (new KafkaProducerService(kafkaTemplate, new KafkaProperties())).sendMessage(message);

    verify(kafkaTemplate).send(isNull(), eq(message));
  }

  @Test
  void testSendMessageFuture() {
    CompletableFuture<Void> completableFuture = new CompletableFuture<>();
    completableFuture.obtrudeException(new Throwable());
    KafkaCompletableFuture<SendResult<String, String>> kafkaCompletableFuture =
        mock(KafkaCompletableFuture.class);
    when(kafkaCompletableFuture.thenAccept(Mockito.<Consumer<SendResult<String, String>>>any()))
        .thenReturn(completableFuture);
    KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    when(kafkaTemplate.send(Mockito.any(), Mockito.any())).thenReturn(kafkaCompletableFuture);

    (new KafkaProducerService(kafkaTemplate, new KafkaProperties())).sendMessage(message);

    verify(kafkaCompletableFuture).thenAccept(isA(Consumer.class));
    verify(kafkaTemplate).send(isNull(), eq(message));
  }

  @Test
  void testFallback() {
    ProducerFactory<String, String> producerFactory = mock(ProducerFactory.class);
    when(producerFactory.transactionCapable()).thenReturn(true);
    KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
    KafkaProducerService kafkaProducerService =
        new KafkaProducerService(kafkaTemplate, new KafkaProperties());

    kafkaProducerService.fallback(message, new Throwable());

    verify(producerFactory).transactionCapable();
  }
}
