package com.kong.konnect.search.config;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.admin.NewTopic;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hegdevageesh
 */
@Configuration
public class AppConfig {
  private final AppConfigProperties appConfigProperties;

  public AppConfig(AppConfigProperties appConfigProperties) {
    this.appConfigProperties = appConfigProperties;
  }

  @Bean
  public NewTopic konnectTopic() {
    return new NewTopic(
        appConfigProperties.getKafka().getTopicName(),
        appConfigProperties.getKafka().getPartitions(),
        appConfigProperties.getKafka().getReplicationFactor());
  }

  @Bean
  public RestHighLevelClient openSearchClient() {
    return new RestHighLevelClient(
        RestClient.builder(
            new HttpHost(
                appConfigProperties.getOpenSearch().getHost(),
                appConfigProperties.getOpenSearch().getPort(),
                appConfigProperties.getOpenSearch().getScheme())));
  }
}
