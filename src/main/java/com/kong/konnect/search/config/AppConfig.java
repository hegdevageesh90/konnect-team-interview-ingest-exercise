package com.kong.konnect.search.config;

import com.kong.konnect.search.config.properties.KafkaProperties;
import com.kong.konnect.search.config.properties.OpenSearchProperties;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.admin.NewTopic;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Generates necessary beans.
 *
 * @author hegdevageesh
 */
@Configuration
public class AppConfig {
  private final KafkaProperties kafkaProperties;
  private final OpenSearchProperties openSearchProperties;

  public AppConfig(KafkaProperties kafkaProperties, OpenSearchProperties openSearchProperties) {
    this.kafkaProperties = kafkaProperties;
    this.openSearchProperties = openSearchProperties;
  }

  @Bean
  public NewTopic konnectTopic() {
    return new NewTopic(
        kafkaProperties.getTopicName(),
        kafkaProperties.getPartitions(),
        kafkaProperties.getReplicationFactor());
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
}
