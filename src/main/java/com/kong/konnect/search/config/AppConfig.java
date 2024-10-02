package com.kong.konnect.search.config;

import com.kong.konnect.search.config.properties.KafkaProperties;
import com.kong.konnect.search.config.properties.OpenSearchProperties;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

/**
 * Generates necessary beans.
 *
 * @author hegdevageesh
 */
@Configuration
public class AppConfig {
  private final OpenSearchProperties openSearchProperties;

  public AppConfig(OpenSearchProperties openSearchProperties) {
    this.openSearchProperties = openSearchProperties;
  }

  @Bean
  public RestHighLevelClient openSearchClient() {
    return new RestHighLevelClient(
        RestClient.builder(
            new HttpHost(
                openSearchProperties.getHost(),
                openSearchProperties.getPort(),
                openSearchProperties.getScheme())));
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    var configProps = new HashMap<String, Object>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
